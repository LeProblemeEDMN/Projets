#version 330

in vec3 position;
in vec2 textureCoords;
in vec3 normal;

out vec3 toCameraVector;
out vec3 worldPos;
out vec3 surfaceNormal;
out vec2 textureCoord2;
out vec3 refractedVector;
out vec3 reflectedVector;

uniform mat4 transformationMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 invViewMatrix;
uniform float useFakeLightning;

void main(void){

	textureCoord2=textureCoords;

	vec4 worldPosition=transformationMatrix*vec4(position,1.0);
	worldPos=worldPosition.xyz;
	vec4 positionRelativToCamera=viewMatrix*worldPosition;
	gl_Position =projectionMatrix* positionRelativToCamera;

	vec3 actualNormal=normal;
	if(useFakeLightning>0.5){
		actualNormal=vec3(0.0,1.0,0.0);
	}
	surfaceNormal=(transformationMatrix*vec4(actualNormal,0.0)).xyz;
	surfaceNormal=normalize(surfaceNormal);

	toCameraVector=(invViewMatrix*vec4(0.0,0.0,0.0,1.0)).xyz - worldPosition.xyz;

	vec3 vecFrag=normalize(-toCameraVector);
	reflectedVector=reflect(vecFrag,surfaceNormal);
	reflectedVector.z=-reflectedVector.z;
	refractedVector=refract(vecFrag,surfaceNormal,1.0/1.33);
}
