#version 330

in vec3 position;
layout (location=5) in mat4 modelMatrix;

uniform mat4 VPMatrix;

void main(void){
	vec4 worldPosition=modelMatrix*vec4(position,1.0);
	gl_Position =VPMatrix* worldPosition;
}