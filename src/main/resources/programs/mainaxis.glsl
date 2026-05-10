//VERTEX_SHADER
#version 450 core

layout(location = 0) in vec4 aPos;
layout(location = 1) in vec4 aCol;

out vec4 vPos;
out vec4 vCol;

uniform mat4 uPerspectiveMatrix;
uniform mat4 uViewMatrix;

void main() {

    vPos = aPos;
    vCol = aCol;

    gl_Position = uPerspectiveMatrix * uViewMatrix * vPos;
}
//FRAGMENT_SHADER
#version 450 core

precision mediump float;

in vec4 vPos;
in vec4 vCol;

out vec4 outFragColor;
void main() {

    outFragColor = vCol;
}