package com.sundayting.wancompose.theme


import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val DefaultTypography = WanTypography()

val LocalWanTypography = staticCompositionLocalOf { DefaultTypography }

val DefaultTextStyle
    get() = TextStyle().copy(
        platformStyle = PlatformTextStyle(
            includeFontPadding = true
        )
    )

val TitleTextStyle
    @Composable
    @ReadOnlyComposable
    get() = WanTheme.typography.h6.copy(
        color = WanTheme.colors.level1TextColor
    )

data class WanTypography(
    val h1: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Light,
        fontSize = 60.sp,
    ),
    val h2: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Light,
        fontSize = 44.sp,
    ),
    val h3: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
    ),
    val h4: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
    ),
    val h5: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
    ),
    val h6: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp,
    ),
    val h7: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp,
    ),
    val h8: TextStyle = DefaultTextStyle.copy(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
    ),
)
