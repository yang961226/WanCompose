package com.sundayting.wancompose.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import com.sundayting.wancompose.common.helper.LocalDarkMode

@Composable
fun WanTheme(
    colors: WanColors = if (LocalDarkMode.current) DarkColors else LightColors,
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
        LocalDarkMode provides false,
    ) {
        content()
    }
}

@Composable
fun AlwaysDarkModeArea(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDarkMode provides true,
    ) {
        content()
    }
}

@Composable
fun ReverseDarkModeArea(
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalDarkMode provides !LocalDarkMode.current,
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