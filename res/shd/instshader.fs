#version 330 core

in vec3 fragmentPosition;
in vec3 fragmentNormal;
in vec4 fragmentColor;

out vec4 outputColor;

void main() {
	vec3 lightPosition = vec3(0.0, 0.0, 0.0);
	vec3 lightVector = lightPosition-fragmentPosition;
	float lightDistance = length(lightVector);
	float lightIntensity = 0.2;
	
	float attenuationFactor = 0.00001;
	float ambientLight = 0.3;
	float diffuseLight = max(dot(fragmentNormal, normalize(lightVector)), 0.0)/(1.0+attenuationFactor*lightDistance*lightDistance);

	outputColor = vec4((ambientLight+diffuseLight*lightIntensity)*fragmentColor.xyz,fragmentColor.w);
}
