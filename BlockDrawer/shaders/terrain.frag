#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
} fs_in;

in vec3 vNormal;
in float light;

uniform sampler2D tex;

void main()
{	
	color = texture(tex, fs_in.tc);
	if (color.w < .01) {
		discard;
	}
	color = color*light;
}