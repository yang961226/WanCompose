package com.sundayting.wancompose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun WanTheme(
    colors: WanColors,
    typography: WanTypography = DefaultTypography,
    content: @Composable () -> Unit,
) {

    CompositionLocalProvider(
        LocalWanColors provides colors,
        LocalWanTypography provides typography
    ) {
        content()
    }

}

@Composable
fun AlwaysLightModeArea(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalWanColors provides LightColors,
    ) {
        content()
    }
}

@Composable
fun AlwaysDarkModeArea(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalWanColors provides DarkColors,
    ) {
        content()
    }
}

object WanTheme {

    val colors: WanColors
        @Composable
        @ReadOnlyComposable
        get() = LocalWanColors.current

    val typography: WanTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalWanTypography.current

}