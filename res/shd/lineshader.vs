#version 330 core

layout(location = 0) in vec3 vertexPosition;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main() {
	gl_Position = projectionMatrix*modelViewMatrix*vec4(vertexPosition, 1.0);
}
