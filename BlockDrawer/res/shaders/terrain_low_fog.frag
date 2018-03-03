#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
} fs_in;

in vec3 vNormal;
in float light;
in vec3 pos;
in vec3 world_pos;

uniform sampler2D tex;

void main()
{	
	color = texture(tex, fs_in.tc);
	if (color.w < .01) {
		discard;
	}
	if (world_pos.y < -50) {
		float fade = pow(2, (world_pos.y + 50) / 20);
		color = color*light*fade + vec4(1, 1, 1, 1) * (1 - fade);
	} else {
		color = color * light;
	}
	color.w = 1;
}