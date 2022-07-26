#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 3) in mat4 instanceMatrix;
layout(location = 8) in uvec4 instanceID;

flat out uvec4 pickingID;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
	pickingID = instanceID;
	gl_Position = projectionMatrix*viewMatrix*instanceMatrix*vec4(vertexPosition, 1.0);
}
