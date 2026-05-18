//VERTEX_SHADER
#version 450 core

layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 aCol;
layout(location = 2) in vec2 aUV;
layout(location = 3) in float aTexIndex;

out vec3 vPos;
out vec3 vCol;
out vec2 vUV;
flat out float vTexIndex;

flat out float topBlock;
flat out float botBlock;
flat out float rigBlock;
flat out float lefBlock;
flat out float topRigBlock;
flat out float topLefBlock;
flat out float botLefBlock;
flat out float botRigBlock;

uniform mat4 uPerspectiveMatrix;
uniform mat4 uViewMatrix;

layout(std140, binding = 0) uniform GlobalData {
    mat4 viewMatrix;
    mat4 projMatrix;
    mat4 orthoMatrix;
    mat4 modelMatrix;
    vec3 ambientLight;              float pad0;
    vec3 skyColor;                  float pad1;
    vec2 screenResolution;          float pad2; float pad3;
    float time;                     float pad4; float pad5; float pad6;
    vec3 playerPosition;            float pad7;
    float chunkDistanceInitFade;    float pad8; float pad9; float pad10;
    float chunkDistanceEndFade;     float pad11; float pad12; float pad13;
} globalData;

void main() {

    int bits = floatBitsToInt(aTexIndex);

    vPos = aPos;
    vCol = aCol;
    vUV = aUV;
    vTexIndex = float((bits >> 0) & 0xFFF);

    float uvX = float((bits >> 13) & 1);
    float uvY = float((bits >> 12) & 1);

    vUV = vec2(uvX, uvY);

    topBlock = float((bits >> 21) & 1);
    botBlock = float((bits >> 20) & 1);
    rigBlock = float((bits >> 19) & 1);
    lefBlock = float((bits >> 18) & 1);
    topRigBlock = float((bits >> 17) & 1);
    topLefBlock = float((bits >> 16) & 1);
    botLefBlock = float((bits >> 15) & 1);
    botRigBlock = float((bits >> 14) & 1);

    //gl_Position = uPerspectiveMatrix * uViewMatrix * vec4(vPos, 1.0);
    gl_Position = globalData.projMatrix * globalData.viewMatrix * vec4(vPos, 1.0);
}
//FRAGMENT_SHADER
#version 450 core

precision mediump float;

in vec3 vPos;
in vec3 vCol;
in vec2 vUV;
flat in float vTexIndex;

flat in float topBlock;
flat in float botBlock;
flat in float rigBlock;
flat in float lefBlock;
flat in float topRigBlock;
flat in float topLefBlock;
flat in float botLefBlock;
flat in float botRigBlock;

out vec4 outFragColor;

uniform sampler2DArray textureSampler;

layout(std140, binding = 0) uniform GlobalData {
    mat4 viewMatrix;
    mat4 projMatrix;
    mat4 orthoMatrix;
    mat4 modelMatrix;
    vec3 ambientLight;              float pad0;
    vec3 skyColor;                  float pad1;
    vec2 screenResolution;          float pad2; float pad3;
    float time;                     float pad4; float pad5; float pad6;
    vec3 playerPosition;            float pad7;
    float chunkDistanceInitFade;    float pad8; float pad9; float pad10;
    float chunkDistanceEndFade;     float pad11; float pad12; float pad13;
} globalData;

void main() {

    vec4 tex = texture(textureSampler, vec3(vUV, vTexIndex));

    if (tex.a < 0.1) discard;

    float fragDistFromPlayer = distance(globalData.playerPosition.xz, vPos.xz);
    if (fragDistFromPlayer > globalData.chunkDistanceEndFade) discard;

    // AMBIENT OCCLUSION
    // =============================================================
    // flat in float topBlock;
    // flat in float botBlock;
    // flat in float rigBlock;
    // flat in float lefBlock;
    // flat in float topRigBlock;
    // flat in float topLefBlock;
    // flat in float botLefBlock;
    // flat in float botRigBlock;

    float ambientOcclusionIntensity = 0.35;
    // Logo abaixo é a versão de occlusion funcinando com cálculo em tempo real.
    tex = tex - tex * topBlock * smoothstep(0.5, 1.0, 1.0 - vUV.y) * ambientOcclusionIntensity;
    tex = tex - tex * botBlock * smoothstep(0.5, 1.0, 0.0 + vUV.y) * ambientOcclusionIntensity;
    tex = tex - tex * rigBlock * smoothstep(0.5, 1.0, 0.0 + vUV.x) * ambientOcclusionIntensity;
    tex = tex - tex * lefBlock * smoothstep(0.5, 1.0, 1.0 - vUV.x) * ambientOcclusionIntensity;
    tex = tex - tex * topRigBlock * smoothstep(0.5, 1.0, 0.0 + vUV.x * (1.0 - vUV.y)) * ambientOcclusionIntensity * 0.6;
    tex = tex - tex * topLefBlock * smoothstep(0.5, 1.0, 1.0 - vUV.x + (0.0 - vUV.y)) * ambientOcclusionIntensity * 0.6;
    tex = tex - tex * botLefBlock * smoothstep(0.5, 1.0, 0.0 + vUV.y - vUV.x) * ambientOcclusionIntensity * 0.6;
    tex = tex - tex * botRigBlock * smoothstep(0.5, 1.0, 0.0 + vUV.x * vUV.y) * ambientOcclusionIntensity * 0.6;

    // Ambient occlusion com aplicação da textura.
//    tex = tex - tex * topBlock * (1.0 - texture(textureSampler, vec3(vUV, 20.0))) * ambientOcclusionIntensity;
//    tex = tex - tex * botBlock * (1.0 - texture(textureSampler, vec3(vUV, 21.0))) * ambientOcclusionIntensity;
//    tex = tex - tex * rigBlock * (1.0 - texture(textureSampler, vec3(vUV, 22.0))) * ambientOcclusionIntensity;
//    tex = tex - tex * lefBlock * (1.0 - texture(textureSampler, vec3(vUV, 23.0))) * ambientOcclusionIntensity;
//    tex = tex - tex * topRigBlock * (1.0 - texture(textureSampler, vec3(vUV, 24.0))) * ambientOcclusionIntensity;
//    tex = tex - tex * topLefBlock * (1.0 - texture(textureSampler, vec3(vUV, 25.0))) * ambientOcclusionIntensity;
//    tex = tex - tex * botLefBlock * (1.0 - texture(textureSampler, vec3(vUV, 26.2))) * ambientOcclusionIntensity;
//    tex = tex - tex * botRigBlock * (1.0 - texture(textureSampler, vec3(vUV, 27.0))) * ambientOcclusionIntensity;

    float fadeStep = smoothstep(globalData.chunkDistanceInitFade, globalData.chunkDistanceEndFade, fragDistFromPlayer);

    vec3 finalColor = mix(
        tex.rgb * globalData.ambientLight,
        globalData.skyColor,
        fadestep
    );

    outFragColor = vec4(finalColor, tex.a);
}