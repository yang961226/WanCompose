package com.sundayting.wancompose.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LightColors = lightColors()
val DarkColors = darkColors()

val CollectColor = Color(0xFFCE2D1F)

fun lightColors(
    primaryColor: Color = Color(0xFF5d737e),
    level1BackgroundColor: Color = Color(0xFFFFFFFF),
    level2BackgroundColor: Color = Color(0xFFF5F5F5),
    level3BackgroundColor: Color = Color(0xFFE8E8E8),
    level4BackgroundColor: Color = Color(0xFFE0E0E0),
    level1TextColor: Color = Color(0XFF212121),
    level2TextColor: Color = Color(0xFF757575),
    level3TextColor: Color = Color(0xFF9E9E9E),
    level4TextColor: Color = Color(0xFFE0E0E0),
    tipColor: Color = Color(0xFF55b978),
    errorColor: Color = Color(0xFFFF7272),
): WanColors {
    return WanColors(
        primaryColor,
        level1BackgroundColor,
        level2BackgroundColor,
        level3BackgroundColor,
        level4BackgroundColor,
        level1TextColor,
        level2TextColor,
        level3TextColor,
        level4TextColor,
        tipColor,
        errorColor
    )
}

fun darkColors(
    primaryColor: Color = Color(0xFF5d737e),
    level1BackgroundColor: Color = Color(0xFF191919),
    level2BackgroundColor: Color = Color(0xFF252525),
    level3BackgroundColor: Color = Color(0xFF303030),
    level4BackgroundColor: Color = Color(0xFF424242),
    level1TextColor: Color = Color(0XFFF5F5F5),
    level2TextColor: Color = Color(0xFFDBDBDB),
    level3TextColor: Color = Color(0xFF999999),
    level4TextColor: Color = Color(0xFF666666),
    tipColor: Color = Color(0xFF36A15C),
    errorColor: Color = Color(0xFFEC4545),
): WanColors {
    return WanColors(
        primaryColor,
        level1BackgroundColor,
        level2BackgroundColor,
        level3BackgroundColor,
        level4BackgroundColor,
        level1TextColor,
        level2TextColor,
        level3TextColor,
        level4TextColor,
        tipColor,
        errorColor
    )
}

data class WanColors(
    val primaryColor: Color,

    val level1BackgroundColor: Color,
    val level2BackgroundColor: Color,
    val level3BackgroundColor: Color,
    val level4BackgroundColor: Color,

    val level1TextColor: Color,
    val level2TextColor: Color,
    val level3TextColor: Color,
    val level4TextColor: Color,

    val tipColor: Color,
    val errorColor: Color,
)

val LocalWanColors = staticCompositionLocalOf { lightColors() }