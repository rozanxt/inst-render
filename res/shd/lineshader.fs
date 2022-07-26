#version 330 core

out vec4 outputColor;

uniform vec4 uniformColor;

void main() {
	outputColor = uniformColor;
}
