#version 330

in vec3 position;
in vec2 textureCoords;
in vec3 normal;
in vec3 tangent;

out vec3 toCameraVector;
out vec3 worldPos;
out vec3 surfaceNormal;
out vec2 textureCoord2;
out vec3 vecFrag;
out mat3 toTangentSpace;
out mat3 invToTangentSpace;

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
	vec3 surfaceNormal=(viewMatrix*transformationMatrix*vec4(actualNormal,0.0)).xyz;
	surfaceNormal=normalize(surfaceNormal);

	vec3 norm = surfaceNormal;
	vec3 tang = normalize((viewMatrix*transformationMatrix * vec4(tangent, 0.0)).xyz);
	vec3 bitang = normalize(cross(norm, tang));

	toTangentSpace = mat3(
	tang.x, bitang.x, norm.x,
	tang.y, bitang.y, norm.y,
	tang.z, bitang.z, norm.z
	);
	invToTangentSpace=inverse(toTangentSpace);
	surfaceNormal=normalize(transformationMatrix*vec4(actualNormal,0.0)).xyz;
	toCameraVector=toTangentSpace*(-positionRelativToCamera.xyz);
	vecFrag=normalize(worldPosition.xyz-(invViewMatrix*vec4(0.0,0.0,0.0,1.0)).xyz);
}
