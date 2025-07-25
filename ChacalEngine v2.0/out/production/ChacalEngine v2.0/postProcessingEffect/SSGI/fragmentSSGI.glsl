#version 330 core

//BEGINNING_INIT_CODE:
//Matrix4f invProjMat = new Matrix4f();
//Pipeline.getProjMat().invert(invProjMat);
//Random rdm=new Random(0);
//for (int i = 0; i < Constantes.SSGI_NUMBER_SAMPLES.getInt(); i++) {
//    Vector3 sample=new Vector3(rdm.nextFloat()*2-1,rdm.nextFloat()*2-1,rdm.nextFloat()*2-1).normalize();
//    samples.loadVector3(sample,i);
//      System.out.println(sample);
//}
//END_INIT_CODE

//BEGINNING_RESIZE_CODE:
//Matrix4f invProjMat = new Matrix4f();
//Pipeline.getProjMat().invert(invProjMat);
//END_RESIZE_CODE

in vec2 TexCoords; // Coordonnées de texture transmises par le vertex shader

out vec4 out_colour;

//textures multisamples
uniform sampler2D lightTexture;//INIT_VALUE:0
uniform sampler2DMS normalTexture;//INIT_VALUE:1
uniform sampler2DMS AMRText;//INIT_VALUE:2
uniform sampler2DMS depthTex;//INIT_VALUE:3

//matrix
uniform mat4 invProjMatrix;//RESIZE_INIT_VALUE:invProjMat
uniform mat4 invViewMatrix;
//screen size
uniform vec2 screenSize;//INIT_RESIZE_VALUE:new Vector2f(DisplayManager.getWidth(),DisplayManager.getHeight())

uniform vec2 linearize_param;//RESIZE_INIT_VALUE:new Vector2f(Pipeline.getProjMat().m22(),Pipeline.getProjMat().m32())
uniform vec3 cameraPosition;

//skymap informations
uniform samplerCube skyMap;//INIT_VALUE:4

// Tableau de points d’échantillonnage dans l'espace tangent
uniform vec3 samples[64];
// Matrice de projection utilisée pour reconstruire la position
uniform mat4 projectionMatrix;//RESIZE_INIT_VALUE:Pipeline.getProjMat()
uniform mat4 projectionViewMatrix;


const float PI = 3.14159265359;


float linearize_depth(float depth){
    return linearize_param.y/(linearize_param.x+depth);
}

vec3 SSGI(float depth,vec3 worldPos,vec3 normal){

    vec4 projP=projectionViewMatrix*vec4(worldPos,1);

    float maxDistance=100;

    vec3 result=vec3(0);

    int number_ray=60;
    vec3 reflected_beam=normalize(reflect(worldPos-cameraPosition,normal));
    int count_ray=0;//we count ray that hit
    //float lin_depth=linearize_depth(depth);
    for(int i=0;i<number_ray;i++){
        vec3 direction=samples[i];//samples[i];
        if(dot(direction,normal)<0)direction*=-1;
        vec4 projD=projectionViewMatrix*vec4(direction,0);
        float currentDistance=0;
        float dt=1.5;
        int found=0;
        while(currentDistance<maxDistance){
            currentDistance+=dt;
            vec4 newScreenPos=projP+currentDistance*projD;
            newScreenPos.xyz=(newScreenPos.xyz/newScreenPos.w)*0.5+0.5;
            if(newScreenPos.x<0 || newScreenPos.x>1  ||newScreenPos.y<0 || newScreenPos.y>1 || newScreenPos.z<0 ){
                //stop the loop
                //result=vec3(1,0,0);
                found=-1;
                currentDistance+=maxDistance;
            }else {
                ivec2 screenCoords=ivec2(screenSize*newScreenPos.xy);
                float depthNewPos=texelFetch(depthTex,screenCoords, 0).r;
                float text_depth=linearize_depth(depthNewPos);
                float pos_depth=linearize_depth(newScreenPos.z);

                //TODO INTERPOLER INTERSECTION

                if (text_depth<pos_depth+max(0.0001,0.005*dt) && text_depth>pos_depth-max(1,1.2*dt)){
                    vec3 unitNormalPixel=normalize(texelFetch(normalTexture, screenCoords, 0).xyz*2-1);
                    if (dot(direction, unitNormalPixel)<0){
                        vec3 albedo=texture(lightTexture,newScreenPos.xy).xyz;//texelFetch(lightTexture, screenCoords, 0).xyz;
                        float coeff=pow(max(0,dot(normal,direction)),15);
                        float coeff_dist=1.0/(currentDistance*currentDistance);
                        result+=albedo;//accept

                        found=1;
                    }

                    //stop the loop
                    currentDistance+=maxDistance;
                }
            }
            //if(currentDistance>200)dt=2;
            //dt*=1.005;
        }
        if(found==0){
            float coeff=pow(max(0,dot(normal,direction)),15);
            float coeff_dist=1.0/(maxDistance*maxDistance);
            result+=texture(skyMap, direction).rgb;
        }
        if(found>=0)count_ray+=1;
    }

    return result/(0.0001+count_ray);//(0.0001+count_ray);
}


void main() {
    //TODO USE SKYBOX WITH LIGHTNING NOT JUST THE FLAT TEXTURE

    ivec2 pixelCoords = ivec2(TexCoords.x * screenSize.x,
    TexCoords.y * screenSize.y);
    float depth=texelFetch(depthTex,pixelCoords, 0).r;

    //Do the skybox
    if(depth<1){
        vec3 unitNormal=normalize(texelFetch(normalTexture,pixelCoords, 0).xyz*2-1);
        vec4 viewPos=invProjMatrix*vec4(TexCoords*2-1,depth*2-1,1);
        viewPos/=viewPos.w;
        vec3 worldPos=(invViewMatrix*viewPos).xyz;

        vec3 unitCameraVector=normalize(cameraPosition-worldPos);
        vec3 albedo=texture(lightTexture,TexCoords.xy).xyz;//texelFetch(lightTexture, screenCoords, 0).xyz;

        out_colour=vec4(SSGI(depth,worldPos,unitNormal)*0.5+albedo,1);//


    }
}