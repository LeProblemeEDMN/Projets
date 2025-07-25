#version 330 core

in vec2 TexCoords; // Coordonnées de texture transmises par le vertex shader

out vec4 out_colour;

//BEGINNING_INIT_CODE:float ssao_bias,float ssao_radius
//Matrix4f invProjMat = new Matrix4f();
//Pipeline.getProjMat().invert(invProjMat);
//Random rdm=new Random(0);
//for (int i = 0; i < Constantes.SSAO_NUMBER_SAMPLES.getInt(); i++) {
//    Vector3 sample=new Vector3(rdm.nextFloat()*2-1,rdm.nextFloat()*2-1,rdm.nextFloat()*2-1).normalize();
//float length=(float) (rdm.nextFloat()*rdm.nextFloat())*ssao_radius;
//sample.mul(length);
//samples.loadVector3(sample,i);
//}
//END_INIT_CODE

//BEGINNING_RESIZE_CODE:
//Matrix4f invProjMat = new Matrix4f();
//Pipeline.getProjMat().invert(invProjMat);
//END_RESIZE_CODE

//textures multisamples
uniform sampler2DMS albedoTexture;//INIT_VALUE:0
uniform sampler2DMS normalTexture;//INIT_VALUE:1
uniform sampler2DMS AMRText;//INIT_VALUE:2
uniform sampler2DMS depthTex;//INIT_VALUE:3

//matrix
uniform mat4 projectionMatrix;//RESIZE_INIT_VALUE:Pipeline.getProjMat()
uniform mat4 projectionViewMatrix;
uniform mat4 invProjMatrix;//RESIZE_INIT_VALUE:invProjMat
uniform mat4 invViewMatrix;
//screen size
uniform vec2 screenSize;//RESIZE_INIT_VALUE:new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight())

uniform vec2 linearize_param;//RESIZE_INIT_VALUE:new Vector2f(Pipeline.getProjMat().m22(),Pipeline.getProjMat().m32())
uniform vec3 cameraPosition;

//skymap informations
uniform samplerCube skyMap;//INIT_VALUE:4

//fog parameters
uniform vec3 fogColor;//INIT_VALUE:Constantes.SKY_COLOR.getVector3()
uniform float fogDensity;//INIT_VALUE:Constantes.DENSITY.getFloat()
uniform float fogGradient;//INIT_VALUE:Constantes.GRADIENT.getFloat()


uniform vec3 samples[64];     // Tableau de points d’échantillonnage dans l'espace tangent

// Rayon d'échantillonnage pour le SSAO
uniform float radius;//INIT_VALUE:ssao_radius
// Biais pour éviter l'auto-occlusion
uniform float bias;//INIT_VALUE:ssao_bias

const float lower_limit=-30.0;
const float upper_limit=40.0;

//spot light structure
struct Spot {
    vec3 attenuation;
    vec3 lookVec;
    float angle;
    float cos_angle;
    vec3 color;
    float far;
};

//point light structure
struct PointLight {
    vec3 attenuation;
    vec3 color;
    float far;
};

//lights (oversized buffer)
uniform PointLight[50] points;
uniform Spot[50] spots;

//positions and shadow matrix of the spots
uniform vec3[50] positionSpot;
//position of point lights
uniform vec3[50] positionPoint;

//number of lights really present in the world
uniform int numberSpots;
uniform int numberPoints;

//cascaded shadow map textures
uniform sampler2D[4] sunTexture;//INIT_VALUE:i+5
//parameter of the cascaded shadow map used to find the id of the shadow map used to draw shadows
uniform float d0_shadow;
uniform float r_shadow;
//shadow map size
uniform vec2 mapSize;//INIT_VALUE:new Vector2f(Constantes.SHADOW_MAP_SIZE.getInt(), Constantes.SHADOW_MAP_SIZE.getInt())
//shadow space matrix
uniform mat4 toShadowMapSpace[4];

const float PI = 3.14159265359;

float sunShadow(int id_map,vec3 worldPos);
float DistributionGGX(vec3 N, vec3 H, float a4);
float GeometrySchlickGGX(float NdotV,float k);
vec3 fresnelSchlick(float cosTheta, vec3 F0);
vec3 PBR_lightning(vec3 albedo,vec3 toLightVector,vec3 lightColor,vec3 attenuation,vec3 unitCameraVector,vec3 unitNormal,vec3 F0,float metallic,float k,float a4);
float SSAO(float depth, vec3 fragPos);

vec3 applyFog( in vec3  rgb,      // original color of the pixel
               in float fogAmount, // camera to point distance
               in vec3  rayDir,   // camera to point vector
               in vec3  sunDir )  // sun light direction
{
    vec3  fog  =fogColor;// mix( fogColor, // bluishlightColor, // yellowishpow(sunAmount,5.0) );
    return mix( rgb, fog, fogAmount );
}

float linearize_depth(float depth){
    return linearize_param.y/(linearize_param.x+depth);
}

vec3 SSGI(float depth,vec3 worldPos,vec3 normal){

    vec4 projP=projectionViewMatrix*vec4(worldPos,1);

    float maxDistance=300;

    vec3 result=vec3(0);

    int number_ray=60;
    //float lin_depth=linearize_depth(depth);
    for(int i=0;i<number_ray;i++){
        vec3 direction=samples[i];
        if(dot(direction,normal)<0)direction*=-1;
        vec4 projD=projectionViewMatrix*vec4(direction,0);
        float currentDistance=0;
        float dt=0.5;
        int found=0;
        while(currentDistance<maxDistance){
            currentDistance+=dt;
            vec4 newScreenPos=projP+currentDistance*projD;
            newScreenPos.xyz=(newScreenPos.xyz/newScreenPos.w)*0.5+0.5;
            if(newScreenPos.x<0 || newScreenPos.x>1  ||newScreenPos.y<0 || newScreenPos.y>1 || newScreenPos.z<0 ){
                //stop the loop
                //result=vec3(1,0,0);
                currentDistance+=maxDistance;
            }else {
                ivec2 screenCoords=ivec2(screenSize*newScreenPos.xy);
                float depthNewPos=texelFetch(depthTex,screenCoords, 0).r;
                float text_depth=linearize_depth(depthNewPos);
                float pos_depth=linearize_depth(newScreenPos.z);
                if (text_depth<pos_depth+0.05*dt && text_depth>pos_depth-1.2*dt){
                    vec3 unitNormalPixel=normalize(texelFetch(normalTexture, screenCoords, 0).xyz*2-1);
                    if (dot(direction, unitNormalPixel)<0){
                        vec3 albedo=texelFetch(albedoTexture, screenCoords, 0).xyz;
                        result+=albedo;//accept
                        found=1;
                    }
                    //stop the loop
                    currentDistance+=maxDistance;
                }
            }
            //if(currentDistance>200)dt=2;
            dt*=1.05;
        }
        if(found==0)result+=texture(skyMap, direction).rgb;
    }


    return result/number_ray;
}


void main() {
    ivec2 pixelCoords = ivec2(TexCoords.x * screenSize.x,
                              TexCoords.y * screenSize.y);
    float depth=texelFetch(depthTex,pixelCoords, 0).r;
    vec4 viewPos=invProjMatrix*vec4(TexCoords*2-1,depth*2-1,1);
    viewPos/=viewPos.w;
    vec3 worldPos=(invViewMatrix*viewPos).xyz;

    //Do the skybox
    if(depth==1){
        //TODO GET SUN POS
        vec3 lightPos=vec3(0,105,0);
        vec3 toLightVector=lightPos-worldPos;
        out_colour = texture(skyMap, worldPos-cameraPosition);
        float factor=(cameraPosition.y-lower_limit)/(upper_limit-lower_limit);
        factor=clamp(factor,0,1);
        out_colour.rgb =2*applyFog(out_colour.rgb,1-factor,normalize(cameraPosition),normalize(toLightVector));
        out_colour.a=1;
    }else{
        vec3 albedo=texelFetch(albedoTexture,pixelCoords, 0).xyz;
        vec3 unitNormal=normalize(texelFetch(normalTexture,pixelCoords, 0).xyz*2-1);

        vec3 unitCameraVector=normalize(cameraPosition-worldPos);
        //if of the shadowmap
        int id_shadow_int=int(log(1+viewPos.z/d0_shadow*(1-r_shadow))/log(r_shadow));
        float sunShadow_factor=sunShadow(id_shadow_int,worldPos);

        //PBR constants
        vec3 arm_values=texelFetch(AMRText,pixelCoords, 0).rgb;
        float r = (arm_values.y + 1.0);
        float k = (r*r) / 8.0;
        float a2      = arm_values.y*arm_values.y;
        float a4     = a2*a2;
        float metallic=arm_values.z;

        vec3 F0 = vec3(0.04);
        F0 = mix(F0, albedo.rgb, metallic);
        vec3 Lo = vec3(0.0);

        //-----------SPOTS------------------------------------------------
        //On peut utiliser au maximum 5 spots light/objet
        for(int i=0;i<numberSpots;i++){
            vec3 dirVec=spots[i].lookVec;
            vec3 toLightVector= (positionSpot[i] -worldPos);
            float distance=length(toLightVector);
            vec3 unitLightVector=normalize(toLightVector);
            float ac=dot(-normalize(positionSpot[i]-worldPos),dirVec);

            //on check qu'on est bien dans l'angle de vue du spot.
            if(ac>spots[i].cos_angle && distance<spots[i].far){

                //smooth border of the spot
                float shadowFactor=min(1,exp(50*(spots[i].angle-0.05-ac)));
                //TODO OMBRES

                Lo+=PBR_lightning(albedo,toLightVector,spots[i].color,spots[i].attenuation,unitCameraVector,unitNormal,F0,metallic,k,a4)*shadowFactor;
            }
        }

        //-----------POINTS --------------------------------------
        // on peut utiliser jusqu'à 5 point light. (0 est le soleil).
        //
        for(int i=0;i<numberPoints;i++){
            vec3 toLightVector= (positionPoint[i] -worldPos);

            float distance=length(toLightVector);
            if(distance<points[i].far){
                vec3 unitLightVector=normalize(toLightVector);
                float shadowFactor=1;

                if(i==0)shadowFactor=sunShadow_factor;
                //TODO OMBRES
                Lo+=PBR_lightning(albedo,toLightVector,points[i].color,points[i].attenuation,unitCameraVector,unitNormal,F0,metallic,k,a4)*shadowFactor;
            }
        }
        //---------------------FIN LUMIERES-----------------------------------

        //bizarre que ce soit pas *2-1
        vec4 occlusion_pos=(invProjMatrix*vec4(TexCoords*2-1,depth,1));
        //ambient occlusion obtained with SSAO
        float ambientOcclusion=SSAO(depth,occlusion_pos.xyz/occlusion_pos.w)*pow(arm_values.x,2);


        out_colour =0.1*vec4(albedo,1)*ambientOcclusion+0.9*vec4(Lo,1);
        //mix the light color and the sky color
        float visibility=exp(-pow((length(viewPos)*fogDensity),fogGradient));
        visibility=clamp(visibility,0.0,1.0);
        out_colour =mix(vec4(fogColor,1.0),out_colour,visibility);


       // out_colour=vec4( sunShadow_factor,0,0,1);
    }


}


//Compute the shadow with shadow mapping
float sunShadow(int id_map,vec3 worldPos){
    //check if we are inside the box.
    vec4 shadowCoords=toShadowMapSpace[id_map]*vec4(worldPos,1);
    if(shadowCoords.x<0 || shadowCoords.x>1  ||shadowCoords.y<0 || shadowCoords.y>1 || shadowCoords.z<0 )return 1.0;//|| shadowCoords[id_map].z>1
    //if it is not the closest shadow map we dont do soft shadows

    if(id_map>0){
        float objectNearestLight=texture(sunTexture[id_map],shadowCoords.xy).r;
        if(shadowCoords.z-0.0005<objectNearestLight){
            return 1.0;
        }
        return 0.0;
    }

    vec3 val=texture(sunTexture[id_map],shadowCoords.xy).rgb;
    float kuns=val.x-shadowCoords.z+0.0005;
    float k=abs(kuns);
    float var=max(val.y-val.x*val.x,0.00002);
    float chebytchev=(var)/(var+k*k);
    float indic=0.5+0.5*kuns/k;//incicatrice 1 si a la lumiere 0 sinon
    return pow(indic+(1-indic)*chebytchev,2);
}

float DistributionGGX(vec3 N, vec3 H, float a4)
{
    float NdotH  = max(dot(N, H), 0.0);
    float NdotH2 = NdotH*NdotH;

    float denom = (NdotH2 * (a4 - 1.0) + 1.0);
    denom = PI * denom * denom;

    return a4 / denom;
}

float GeometrySchlickGGX(float NdotV,float k)
{
    float num   = NdotV;
    float denom = NdotV * (1.0 - k) + k;

    return num / denom;
}

vec3 fresnelSchlick(float cosTheta, vec3 F0)
{
    return F0 + (1.0 - F0) * pow(clamp(1.0 - cosTheta, 0.0, 1.0), 5.0);
}

vec3 PBR_lightning(vec3 albedo,vec3 toLightVector,vec3 lightColor,vec3 attenuation,vec3 unitCameraVector,vec3 unitNormal,vec3 F0,float metallic,float k,float a4){
    //albedo: vec3 texture colour.
    //toLightVector: vec3 of light position to which we substract the object position
    //attenuation: vec3 attenuation coefficients.
    //lightColor: color of light
    //unitCameraVector: vec3 normalize of the vector going from fragment to camera.
    //unitNormal: normalized normal
    //F0: parmater of fresnelSchlick (color if metallic and 0.04 otherwise)
    //metallic: gow metallic is the material
    //k: parameter of GeometrySchlickGGX.
    //a4: parameter of DistributionGGX (roughness^4).
    // calculate per-light radiance
    vec3 L = normalize(toLightVector);
    vec3 H = normalize(unitCameraVector + L);
    float distance    = length(toLightVector);
    float attenuation_value = 1.0 / (attenuation.x+attenuation.y*distance+attenuation.z*distance * distance);
    vec3 radiance     = lightColor * attenuation_value;

    float NdotV=max(dot(unitNormal, unitCameraVector), 0.0);
    float NdotL = max(dot(unitNormal, L), 0.0);

    // cook-torrance brdf
    float NDF = DistributionGGX(unitNormal, H, a4);
    float G   = GeometrySchlickGGX(NdotV,k)*GeometrySchlickGGX(NdotL,k);
    vec3 F    = fresnelSchlick(max(dot(H, unitCameraVector), 0.0), F0);

    vec3 kS = F;
    vec3 kD = vec3(1.0) - kS;
    kD *= 1.0 - metallic;

    vec3 numerator    = NDF * G * F;
    float denominator = 4.0 * NdotV * NdotL + 0.0001;
    vec3 specular     = numerator / denominator;

    // add to outgoing radiance Lo
    return  (kD * albedo / PI + specular) * radiance * NdotL;
}

float SSAO(float depth, vec3 fragPos){
    // Calcul de l’occlusion ambiante
    float occlusion = 0.0;


    for (int i = 0; i < 64; ++i) {
        // Position de l'échantillon dans l'espace tangent
        vec3 sampled = fragPos + samples[i];
        //ssao = sample.x;
        // Reprojection dans l'espace écran
        vec4 offset = projectionMatrix * vec4(sampled, 1.0);
        offset.xyz /= offset.w; // Perspective division
        offset.xy = offset.xy * 0.5 + 0.5; // De clip space (-1,1) à NDC (0,1)

        // Profondeur du pixel échantillonné
        ivec2 pixelCoords = ivec2(offset.x * screenSize.x,
        offset.y * screenSize.y);
        float sampleDepth = texelFetch(depthTex,pixelCoords, 0).r;
        // Reprojeté en profondeur espace vue
        float rangeCheck = smoothstep(0.0, 1.0, radius / abs(samples[i].z));
        if (sampleDepth > offset.z - bias) {
            occlusion += rangeCheck; // Accumuler l’occlusion
        }

    }

    occlusion =  ((occlusion) / 64.0); // Normalisation et inversion
    return occlusion;
}