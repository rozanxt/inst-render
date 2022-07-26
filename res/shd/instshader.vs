#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 2) in vec3 vertexNormal;
layout(location = 3) in mat4 instanceMatrix;
layout(location = 7) in vec4 instanceColor;

out vec3 fragmentPosition;
out vec3 fragmentNormal;
out vec4 fragmentColor;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main() {
	mat4 modelViewMatrix = viewMatrix*instanceMatrix;
	fragmentPosition = vec3(modelViewMatrix*vec4(vertexPosition, 1.0));
	fragmentNormal = vec3(modelViewMatrix*vec4(vertexNormal, 0.0));
	fragmentColor = instanceColor;
	gl_Position = projectionMatrix*vec4(fragmentPosition, 1.0);
}
