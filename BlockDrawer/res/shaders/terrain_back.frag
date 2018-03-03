#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
} fs_in;

in vec3 vNormal;
in float light;
in vec3 pos;

uniform sampler2D tex;

void main()
{	
	color = texture(tex, fs_in.tc);
	if (color.w < .01) {
		discard;
	}
	float fade = pow(2, -pos.z / 100);
	color = color*light*fade + vec4(1, 1, 1, 1) * (1 - fade);
	color.w = 1;
}