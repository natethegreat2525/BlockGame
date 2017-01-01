#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
} fs_in;

in vec3 vNormal;

uniform sampler2D tex;

struct SimpleDirectionalLight
{
	vec3 vColor;
	vec3 vDirection;
	float fAmbientIntensity;
};

uniform SimpleDirectionalLight sunLight;

void main()
{	
	color = texture(tex, fs_in.tc);
	if (color.w < .01) {
		//color = vec4(0, .2, 0, 1);
		discard;
	}
	float fDiffuseIntensity = max(0.0, dot(normalize(vNormal), -sunLight.vDirection)); 
	color = color*vec4(sunLight.vColor*(sunLight.fAmbientIntensity+fDiffuseIntensity), 1.0);
}