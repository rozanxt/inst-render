#version 330 core

layout(location = 0) in vec3 vertexPosition;
layout(location = 1) in vec2 vertexTexCoord;

out vec2 fragmentTexCoord;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

void main() {
	fragmentTexCoord = vertexTexCoord;
	gl_Position = projectionMatrix*modelViewMatrix*vec4(vertexPosition, 1.0);
}
