package com.sundayting.wancompose.page.scan

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.WanComposeDestination

object ScanScreen : WanComposeDestination {
    override val route: String
        get() = "扫描页面"


    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
    ) {

        ConstraintLayout(
            modifier
                .background(Color.Black)
                .pointerInput(Unit) {}
        ) {

        }

    }

    fun NavController.navigateToScanScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }
}