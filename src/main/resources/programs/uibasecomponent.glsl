//VERTEX_SHADER
#version 450 core

layout(location = 0) in vec4 aPosition;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aUV;
layout(location = 3) in float aTextureIndex;
layout(location = 4) in float aCornerCurveSize;

const int UI_BASE_COMPONENT = 0;
const int UI_BUTTON_COMPONENT = 1;
const int UI_SLIDER_COMPONENT = 2;
const int UI_CHECK_BOX_COMPONENT = 3;
const int UI_TEXT_COMPONENT = 4;
const int UI_TEXT_FIELD_COMPONENT = 5;
const int UI_COLOR_PICKER_COMPONENT = 6;
const int UI_IMAGE_COMPONENT = 7;
const int UI_LAYOUT_VERTICAL_COMPONENT = 8;
const int UI_LAYOUT_HORIZONTAL_COMPONENT = 9;
const int UI_SPACER_COMPONENT = 10;

out vec4 vPosition;
out vec4 vColor;
out vec2 vUV;
flat out float vTextureIndex;
flat out float vCornerCurveSize;

flat out int vIsShowingBorder;
flat out int vIsClicked;
flat out int vIsOnFocus;
flat out int vIsHovered;

uniform mat4 uOrthoMatrix;
uniform mat4 uModelMatrix;
uniform int uBaseUIStates;

void main() {

    vPosition = aPosition;
    vColor = aColor;
    vUV = aUV;
    vTextureIndex = aTextureIndex;
    vCornerCurveSize = aCornerCurveSize;

    vIsShowingBorder = (uBaseUIStates >> 0) & 1;
    vIsClicked = (uBaseUIStates >> 1) & 1;
    vIsOnFocus = (uBaseUIStates >> 2) & 1;
    vIsHovered = (uBaseUIStates >> 3) & 1;

    gl_Position = uOrthoMatrix * uModelMatrix * vPosition;
}

//FRAGMENT_SHADER
#version 450 core

layout(location = 0) out vec4 outFragColor;

const int UI_BASE_COMPONENT = 0;
const int UI_BUTTON_COMPONENT = 1;
const int UI_SLIDER_COMPONENT = 2;
const int UI_CHECK_BOX_COMPONENT = 3;
const int UI_TEXT_COMPONENT = 4;
const int UI_TEXT_FIELD_COMPONENT = 5;
const int UI_COLOR_PICKER_COMPONENT = 6;
const int UI_IMAGE_COMPONENT = 7;
const int UI_LAYOUT_VERTICAL_COMPONENT = 8;
const int UI_LAYOUT_HORIZONTAL_COMPONENT = 9;
const int UI_SPACER_COMPONENT = 10;

in vec4 vColor;
in vec4 vPosition;
in vec2 vUV;
flat in float vTextureIndex;
flat in float vCornerCurveSize;

flat in int vIsShowingBorder;
flat in int vIsClicked;
flat in int vIsOnFocus;
flat in int vIsHovered;

uniform sampler2D uSampler2DImage;
uniform sampler2DArray uSampler2DArrayGlyphs;

// uiPositionSizeValues -> [posX, posY, width, height].
uniform vec4 uiPositionSizeValues;
// [normalizedX, normalizedY, buttonWidth, buttonHeight].
uniform vec4 uiSliderValues;
// [primitiveType, isChecked, screenWidth, screenHeight].
uniform vec4 uValuesPrimitiveTypeIsCheckViewPortWidthHeight;
// Ratio.
float uiRatio = uiPositionSizeValues.z / uiPositionSizeValues.w;

// Informa qual o tipo de component é a ui, conforme as constantes acima.
int uiPrimitiveType = int (uValuesPrimitiveTypeIsCheckViewPortWidthHeight.x);
// Para sliders, informa o valor normalizado no intervalo [0, 1] da posição do slider.
// Para check boxes, informa se o botão está marcado ou não.
int uIsChecked = int (uValuesPrimitiveTypeIsCheckViewPortWidthHeight.y);
float uSliderNormalizedValueX = uiSliderValues.x;
// Para sliders, informa o tamanho do botão de drag do slider.
float uSliderNormalizedValueY = uiSliderValues.y;
// Para sliders, informa o tamanho do botão de drag do slider.
float uSliderButtonWidth = uiSliderValues.z;
// Para sliders, informa o tamanho do botão de drag do slider.
float uSliderButtonHeight = uiSliderValues.w;
// Width e Height do component.
vec2 uUISizeWidthHeigh = vec2(uiPositionSizeValues.z, uiPositionSizeValues.w);

const float sliderMaxValue = 1.0 - uSliderButtonWidth;

// Métodos para renderização das partes expecíficas de cada component.
void draw_UI_BASE_COMPONENT();
void draw_UI_BUTTON_COMPONENT();
void draw_UI_SLIDER_COMPONENT();
void draw_UI_CHECK_BOX_COMPONENT();
void draw_UI_TEXT_COMPONENT();
void draw_UI_TEXT_FIELD_COMPONENT();
void draw_UI_COLOR_PICKER_COMPONENT();
void draw_UI_IMAGE_COMPONENT();
void draw_UI_LAYOUT_VERTICAL_COMPONENT();
void draw_UI_LAYOUT_HORIZONTAL_COMPONENT();
void draw_UI_SPACER_COMPONENT();

float resultForBackground;
float resultForImage;
float resultForGlyphs;

vec2 cornerTopRight = vec2(+0.5, -0.5);
vec2 cornerTopLeft = vec2(-0.5, -0.5);
vec2 cornerBottomLeft = vec2(-0.5, +0.5);
vec2 cornerBottomRight = vec2(+0.5, +0.5);

vec2 centerTopLeft = vec2(-0.5 + vCornerCurveSize, -0.5 + vCornerCurveSize);
vec2 centerTopRight = vec2(+0.5 - vCornerCurveSize, -0.5 + vCornerCurveSize);
vec2 centerBottomLeft = vec2(-0.5 + vCornerCurveSize, +0.5 - vCornerCurveSize);
vec2 centerBottomRight = vec2(+0.5 - vCornerCurveSize, +0.5 - vCornerCurveSize);

void main() {

    // O valor de resultForBackground é 1 somente quando vTextureIndex é 256.
    // Utilizado para evitar alteraçõs de pixels que não sejam do background.
    resultForBackground = step(256, vTextureIndex) * step(vTextureIndex, 256);

    // O valor de resultForImage é 1 somente quando vTextureIndex é 256.
    // Utilizado para evitar alteraçõs de pixels que não sejam da imagem individual.
    resultForImage = step(257, vTextureIndex) * step(vTextureIndex, 257);

    // O valor de resultForGlyphs é 1 somente quando vTextureIndex é até 255.
    // Utilizado para evitar alteraçõs de pixels que não sejam dos glyphs.
    resultForGlyphs = step(vTextureIndex, 255);

    // Imagem avulsa sobre o background.
    vec4 textureImage = texture(uSampler2DImage, vUV);

    // Background e glyphs.
    vec4 textureGlyphs = texture(uSampler2DArrayGlyphs, vec3(vUV, vTextureIndex));

    //textureGlyphs = textureGlyphs * (1 - resultForGlyphs) + textureGlyphs * (0 + resultForGlyphs) * smoothstep(0.50, 0.4, abs(vUVCenter.x));

    // Resultado para os states do component.
    float vIsHoveredResult = (1.0 - vIsHovered) + 1.5 * vIsHovered;
    float vIsClickedResult = (1.0 - vIsClicked) + 1.5 * vIsClicked;

    // Quando resultForImage é 1, renderiza a imagem individual, do contrário, renderia os glyphs.
    // Assim é possível gerenciar a inclusão do backgroun, de uma imagem e do texto.
    outFragColor = vColor * textureGlyphs * (1 - resultForImage) + textureImage * resultForImage;

    if (uiPrimitiveType == UI_BASE_COMPONENT) {

        draw_UI_BASE_COMPONENT();
    } else if (uiPrimitiveType == UI_BUTTON_COMPONENT) {

        draw_UI_BUTTON_COMPONENT();
    } else if (uiPrimitiveType == UI_SLIDER_COMPONENT) {

        draw_UI_SLIDER_COMPONENT();
    } else if (uiPrimitiveType == UI_CHECK_BOX_COMPONENT) {

        draw_UI_CHECK_BOX_COMPONENT();
    } else if (uiPrimitiveType == UI_TEXT_COMPONENT) {

        draw_UI_TEXT_COMPONENT();
    } else if (uiPrimitiveType == UI_TEXT_FIELD_COMPONENT) {

        draw_UI_TEXT_FIELD_COMPONENT();
    } else if (uiPrimitiveType == UI_COLOR_PICKER_COMPONENT) {

        draw_UI_COLOR_PICKER_COMPONENT();
    } else if (uiPrimitiveType == UI_IMAGE_COMPONENT) {

        draw_UI_IMAGE_COMPONENT();
    } else if (uiPrimitiveType == UI_LAYOUT_VERTICAL_COMPONENT) {

        draw_UI_LAYOUT_VERTICAL_COMPONENT();
    } else if (uiPrimitiveType == UI_LAYOUT_HORIZONTAL_COMPONENT) {

        draw_UI_LAYOUT_HORIZONTAL_COMPONENT();
    } else if (uiPrimitiveType == UI_SPACER_COMPONENT) {

        draw_UI_SPACER_COMPONENT();
    }

    // Construindo os cantos arredondados.
    // Funcionando, mas o UILayoutComponent está com problema.
    // ========================================================================================
//    vec2 vUVCentered;
//
//    vUVCentered.x = vUV.x * ratio - 0.5;
//    vUVCentered.y = vUV.y - 0.5;
//
//    float resultA = step(vUVCentered.x, centerTopLeft.x) * step(vUVCentered.y, centerTopLeft.y);
//    float lengthA = length(vUVCentered - centerTopLeft);
//    float cornerA = step(vCornerCurveSize, lengthA) * resultA * resultForBackground;
//    outFragColor = outFragColor - cornerA;
//
//    float resultD = step(vUVCentered.x, centerBottomLeft.x) * step(centerBottomLeft.y, vUVCentered.y);
//    float lengthD = length(vUVCentered - centerBottomLeft);
//    float cornerD = step(vCornerCurveSize, lengthD) * resultD * resultForBackground;
//    outFragColor = outFragColor - cornerD;
//
//    vUVCentered.x = vUV.x * ratio - ratio + 0.5;
//    vUVCentered.y = vUV.y - 0.5;
//
//    float resultB = step(centerTopRight.x, vUVCentered.x) * step(vUVCentered.y, centerTopRight.y);
//    float lengthB = length(vUVCentered - centerTopRight);
//    float cornerB = step(vCornerCurveSize, lengthB) * resultB * resultForBackground;
//    outFragColor = outFragColor - cornerB;
//
//    float resultE = step(centerBottomRight.x, vUVCentered.x) * step(centerBottomRight.y, vUVCentered.y);
//    float lengthE = length(vUVCentered - centerBottomRight);
//    float cornerE = step(vCornerCurveSize, lengthE) * resultE * resultForBackground;
//    outFragColor = outFragColor - cornerE;
    // ========================================================================================


    // A aplicação do estado do isClicked e isHovered aqui no final garante que a mudança de brilho
    // vai ocorrer apenas sobre os pixels que ficaram, não em algum possível alpha fora da área
    // visível do componente.
    outFragColor = outFragColor * vIsHoveredResult * vIsClickedResult;

    // NESTE PONTO VAMOS IMPLEMENTAR UMA VERIFICAÇÃO DE POSIÇÃO DOS FRAGMENTOS PARA
    // IMPEDIR A EXIBIÇÃO DELES FORA DA ÁREA DO COMPONENTE.
    // ========================================================================================
    // gl_FragCoord inicia em bottom left [0, 0]. e termina em top right [width, heigt].
    // uiPositionSizeValues -> [posX, posY, width, height].
    // uValuesPrimitiveTypeIsCheckViewPortWidthHeight -> [primitiveType, isChecked, screenWidth, screenHeight].
    if (gl_FragCoord.x > uiPositionSizeValues.x + uiPositionSizeValues.z) outFragColor.a = 0;
    if (gl_FragCoord.x < uiPositionSizeValues.x) outFragColor.a = 0;
    if (gl_FragCoord.y > uValuesPrimitiveTypeIsCheckViewPortWidthHeight.w - uiPositionSizeValues.y) outFragColor.a = 0;
    if (gl_FragCoord.y < uValuesPrimitiveTypeIsCheckViewPortWidthHeight.w - uiPositionSizeValues.y - uiPositionSizeValues.w) outFragColor.a = 0;
}

void draw_UI_BASE_COMPONENT() {


}

void draw_UI_BUTTON_COMPONENT() {



}

void draw_UI_SLIDER_COMPONENT() {

    // RENDEREIÇÃO DO BOTÃO DESLIZANTE NO SLIDER.
    float sliderButtonPosition = step(uSliderNormalizedValueX, vUV.x) * step(vUV.x, uSliderNormalizedValueX + uSliderButtonWidth);
    float sliderButtonHeight = step(uSliderButtonWidth, vUV.y) * step(vUV.y, sliderMaxValue);

    outFragColor = outFragColor + outFragColor * sliderButtonPosition * sliderButtonHeight * 2.0;
}

// States auxiliares para renderização do check box.
vec4 checkedColor = vec4(0.0, 0.8, 0.0, 1.0);
vec4 uncheckedColor = vec4(0.8, 0.0, 0.0, 1.0);
float buttonRadius = 0.2;

void draw_UI_CHECK_BOX_COMPONENT() {

    // RENDERIZAÇÃO DO CÍRCULO À DIREITA VERDE/VERMELHO.
    float vUVLength = length(vec2(vUV.x * uiRatio - uiRatio + 0.5, vUV.y - 0.5));
    float checkCenterResult = step(vUVLength, buttonRadius);
    vec4 checkFinalColor =  uncheckedColor * (1 - uIsChecked) + checkedColor * (0 + uIsChecked);
    checkFinalColor *= smoothstep(buttonRadius, buttonRadius - 0.015, vUVLength);

    outFragColor = outFragColor + checkFinalColor * checkCenterResult * resultForBackground;
}

void draw_UI_TEXT_COMPONENT() {


}

void draw_UI_TEXT_FIELD_COMPONENT() {


}

// States auxiliares para renderização do color picker.
vec3 colorPickerBlack = vec3(0.0, 0.0, 0.0);
vec3 colorPickerWhite = vec3(1.0, 1.0, 1.0);

void draw_UI_COLOR_PICKER_COMPONENT() {

    vec3 colorA = mix(colorPickerWhite, vColor.xyz, vUV.x);
    vec3 colorB = mix(colorA, colorPickerBlack, vUV.y);

    float radius = distance(vec2(uSliderNormalizedValueX, uSliderNormalizedValueY), vUV);
    float resultRadius = step(radius, 0.050) * step(0.035, radius);
    vec4 finalColor = vec4(colorB, 1.0);

    outFragColor = finalColor - finalColor * resultRadius;
    outFragColor.a = 1.0;
}

void draw_UI_IMAGE_COMPONENT() {


}

void draw_UI_LAYOUT_VERTICAL_COMPONENT() {


}

void draw_UI_LAYOUT_HORIZONTAL_COMPONENT() {


}

void draw_UI_SPACER_COMPONENT() {


}

/**
 🔹 fract(x) — Fractional Part
 =============================================
 📘 What it does:
 Returns the fractional part of a number. In other words, it subtracts the integer part and leaves the decimal.
 🧮 Formula:
 fract(x) = x - floor(x)
 🧠 Intuition:
 Imagine slicing a cake into whole pieces and crumbs. fract gives you just the crumbs.
 🧪 Examples:
 fract(3.75) → 0.75
 fract(-2.3) → 0.7  // Because floor(-2.3) = -3


 🔹 floor(x) — Round Down
 =============================================
 📘 What it does:
 Returns the largest integer less than or equal to x.
 🧮 Behavior:
 floor(2.9) → 2.0
 floor(-1.2) → -2.0


🔹 step(edge, x) — Binary Threshold
 =============================================
 📘 What it does:
 Returns 0.0 if x < edge, and 1.0 otherwise.
 🧮 Formula:
 step(edge, x) = x < edge ? 0.0 : 1.0
 🧠 Intuition:
 It’s like a light switch: off below the threshold, on above it.
 🧪 Examples:
 step(0.5, 0.3) → 0.0
 step(0.5, 0.7) → 1.0


 🔹 smoothstep(edge0, edge1, x) — Smooth Transition
 =============================================
 📘 What it does:
 Returns a smooth interpolation between 0.0 and 1.0 as x moves from edge0 to edge1.
 🧮 Formula:
 t = clamp((x - edge0) / (edge1 - edge0), 0.0, 1.0)
 smoothstep = t * t * (3.0 - 2.0 * t)
 🧠 Intuition:
 Imagine a fade-in effect: starts slow, speeds up, then slows down again.
 🧪 Examples:
 smoothstep(0.0, 1.0, 0.0) → 0.0
 smoothstep(0.0, 1.0, 0.5) → 0.5
 smoothstep(0.0, 1.0, 1.0) → 1.0

 =============================================
 =============================================
 =============================================
*/