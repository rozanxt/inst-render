#version 330 core

flat in uvec4 pickingID;

out uvec4 outputID;

void main() {
	outputID = pickingID;
}
