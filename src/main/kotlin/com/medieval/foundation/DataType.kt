package com.medieval.foundation

enum class DataType(val size: Int) {
    MAT4(size = 64),        // mat4 Ex.: modelMatrix;          // 64 bytes
    VEC4(size = 16),        // vec4 Ex.: skyColor with alpha;  // 16 bytes (vec4 pads to 16)
    VEC3(size = 16),        // vec3 Ex.: skyColor;             // 16 bytes (vec3 pads to 16)
    VEC2(size = 16),        // vec2 Ex.: screenResolution;     // 16 bytes
    FLOAT(size = 16);       // float Ex.: isPaused;            // 4 bytes, but padded to 16
}