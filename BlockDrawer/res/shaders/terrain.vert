#version 330 core

layout (location = 0) in vec4 position;
layout (location = 1) in vec2 tc;
layout (location = 2) in float lightIn;
layout (location = 3) in vec4 colorIn;

uniform mat4 vw_matrix = mat4(1.0);
uniform mat4 ml_matrix = mat4(1.0);
uniform vec4 sway = vec4(0.0);

out DATA
{
	vec2 tc;
} vs_out;

out float light;
out vec4 colorVal;

void main()
{
	vec4 pos = position;
	pos.w = 1.0;
	vec4 globPos = ml_matrix * pos;
	
	pos.x = pos.x + (position.w * sway.x * sin(globPos.x * sway.y + sway.w));
	pos.z = pos.z + (position.w * sway.z * cos(globPos.z * sway.y + sway.w));
	gl_Position = vw_matrix * ml_matrix * pos;
	vs_out.tc = tc;
	light = lightIn;
	colorVal = colorIn;
}
