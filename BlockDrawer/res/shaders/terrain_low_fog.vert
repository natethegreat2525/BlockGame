#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tc;
layout (location = 2) in float lightIn;

uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);

out DATA
{
	vec2 tc;
} vs_out;

out vec3 pos;
out vec3 world_pos;
out float light;

void main()
{
	world_pos = vec3(ml_matrix * vec4(position, 1.0));
	gl_Position = vw_matrix * ml_matrix * vec4(position, 1.0);
	vs_out.tc = tc;
	light = lightIn;
	pos = vec3(gl_Position);
}