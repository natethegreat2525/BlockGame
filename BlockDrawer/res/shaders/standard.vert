#version 330 core

layout (location = 0) in vec3 position;
layout (location = 1) in vec2 tc;
layout (location = 2) in vec3 norm;

uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform mat4 norm_matrix = mat4(1.0);

out DATA
{
	vec2 tc;
} vs_out;

out vec3 vNormal;

void main()
{
	gl_Position = vw_matrix * ml_matrix * vec4(position, 1.0);
	vs_out.tc = tc;
	vec4 vRes = norm_matrix * vec4(norm, 0.0);
	vNormal = vRes.xyz;
}