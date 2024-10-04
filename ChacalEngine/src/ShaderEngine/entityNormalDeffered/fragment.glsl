#version 330

in vec3 toCameraVector;
in vec3 worldPos;
in vec3 surfaceNormal;
in vec2 textureCoord2;
in vec3 vecFrag;
in mat3 toTangentSpace;
in mat3 invToTangentSpace;

layout(location=0) out vec4 out_Color;
layout(location=1) out vec4 out_vector;
layout(location=2) out vec4 out_pos;
layout(location=3) out vec4 out_autre;

uniform sampler2D modelTexture;
uniform sampler2D normalTexture;
uniform samplerCube environmentMap;
uniform float reflectivity;
uniform float shineDamper;
uniform vec2 materialValue;
uniform vec3 camera;
uniform mat4 invViewMatrixFragment;
void main(void){

	vec3 normalMapValue = 2.0 * texture(normalTexture, textureCoord2).rgb - 1.0;
	vec3 unitNormal = normalize(normalMapValue.rgb);

	vec4 textureColour=texture(modelTexture,textureCoord2);
	if(textureColour.a<0.5){
		discard;
	}
	vec4 norm=vec4(invToTangentSpace*unitNormal,0.0);
	norm=invViewMatrixFragment*norm;

	out_Color =vec4(textureColour.rgb,shineDamper/64);
	out_vector=vec4((vec3(0,1,0).xyz+1)/2,reflectivity/3);
	//
	//out_vector.rgb=inverse(toTangentSpace)*unitNormal;
	out_pos=vec4((worldPos-camera+vec3(256))/512,1);
	out_autre.a=0;
	if(length(materialValue)>0){

		vec4 reflectedColor=texture(environmentMap,reflect(vecFrag,norm.xyz));
		vec4 refractedColor=texture(environmentMap,refract(vecFrag,norm.xyz,1.0/1.33));
		vec3 final=mix(refractedColor.rgb,reflectedColor.rgb,materialValue.x/(materialValue.y+materialValue.x));
		out_autre=vec4(final,(materialValue.y+materialValue.x));

	}
}
