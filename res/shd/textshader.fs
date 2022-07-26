#version 330 core

in vec2 fragmentTexCoord;

out vec4 outputColor;

uniform vec4 uniformColor;
uniform sampler2D textureUnit;

void main() {
	outputColor = uniformColor*texture2D(textureUnit, fragmentTexCoord);
}
