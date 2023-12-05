package com.sundayting.wancompose.page.examplewidgetscreen.pointinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.sundayting.wancompose.WanComposeDestination

object PointInput : WanComposeDestination {
    override val route: String
        get() = "手势"

    fun NavController.navigateToPointInput() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(modifier: Modifier) {
        Box(modifier.background(Color.Red)) {

        }
    }

}