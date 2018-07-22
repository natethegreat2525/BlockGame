#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
} fs_in;

in vec3 vNormal;
in float light;
in vec4 colorVal;

uniform sampler2D tex;
uniform float alpha;

void main()
{
	vec4 texCol = texture(tex, fs_in.tc);
	color = texCol * colorVal * light;
	color.w = colorVal.w * texCol.w * alpha;
	
	if (color.w < .01) {
		discard;
	}
}
