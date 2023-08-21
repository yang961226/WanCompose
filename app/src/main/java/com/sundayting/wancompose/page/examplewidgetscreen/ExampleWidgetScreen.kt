package com.sundayting.wancompose.page.examplewidgetscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sundayting.wancompose.page.homescreen.HomeScreen

object ExampleWidgetScreen : HomeScreen.HomeScreenPage {
    override val route: String
        get() = "代码案例"

    @Composable
    fun Screen(modifier: Modifier = Modifier) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text("代码案例")
        }
    }
}