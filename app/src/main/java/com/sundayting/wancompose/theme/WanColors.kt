package com.sundayting.wancompose.theme

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LightColors = lightColors()
val DarkColors = darkColors()

val CollectColor = Color(0xFFCE2D1F)

val LightPrimaryColor
    get() = Color(0xFF446DF6)
val DarkPrimaryColor
    get() = Color(0xFF4C5270)

internal fun WanColors.updateColorsFrom(other: WanColors) {
    primaryColor = other.primaryColor
    level1BackgroundColor = other.level1BackgroundColor
    level2BackgroundColor = other.level2BackgroundColor
    level3BackgroundColor = other.level3BackgroundColor
    level4BackgroundColor = other.level4BackgroundColor
    level1TextColor = other.level1TextColor
    level2TextColor = other.level2TextColor
    level3TextColor = other.level3TextColor
    level4TextColor = other.level4TextColor
    tipColor = other.tipColor
    errorColor = other.errorColor
}


fun lightColors(
    primaryColor: Color = LightPrimaryColor,
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
    primaryColor: Color = DarkPrimaryColor,
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

@Stable
class WanColors(
    primaryColor: Color,
    level1BackgroundColor: Color,
    level2BackgroundColor: Color,
    level3BackgroundColor: Color,
    level4BackgroundColor: Color,

    level1TextColor: Color,
    level2TextColor: Color,
    level3TextColor: Color,
    level4TextColor: Color,

    tipColor: Color,
    errorColor: Color,
) {


    var primaryColor: Color by mutableStateOf(primaryColor)
        internal set
    var level1BackgroundColor: Color by mutableStateOf(level1BackgroundColor)
        internal set
    var level2BackgroundColor: Color by mutableStateOf(level2BackgroundColor)
        internal set
    var level3BackgroundColor: Color by mutableStateOf(level3BackgroundColor)
        internal set
    var level4BackgroundColor: Color by mutableStateOf(level4BackgroundColor)
        internal set
    var level1TextColor: Color by mutableStateOf(level1TextColor)
        internal set
    var level2TextColor: Color by mutableStateOf(level2TextColor)
        internal set
    var level3TextColor: Color by mutableStateOf(level3TextColor)
        internal set
    var level4TextColor: Color by mutableStateOf(level4TextColor)
        internal set
    var tipColor: Color by mutableStateOf(tipColor)
        internal set
    var errorColor: Color by mutableStateOf(errorColor)
        internal set

    fun copy(
        primaryColor: Color = this.primaryColor,
        level1BackgroundColor: Color = this.level1BackgroundColor,
        level2BackgroundColor: Color = this.level2BackgroundColor,
        level3BackgroundColor: Color = this.level3BackgroundColor,
        level4BackgroundColor: Color = this.level4BackgroundColor,

        level1TextColor: Color = this.level1TextColor,
        level2TextColor: Color = this.level2TextColor,
        level3TextColor: Color = this.level3TextColor,
        level4TextColor: Color = this.level4TextColor,

        tipColor: Color = this.tipColor,
        errorColor: Color = this.errorColor,
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

}

val LocalWanColors = staticCompositionLocalOf { lightColors() }