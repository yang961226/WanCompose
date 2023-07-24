package com.sundayting.wancompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.web.rememberWebViewState
import com.sundayting.wancompose.homescreen.HomeScreen
import com.sundayting.wancompose.web.WebViewScreen
import com.sundayting.wancompose.web.WebViewScreen.navigateToWebViewScreen
import com.sundayting.wancompose.web.WebViewScreen.urlArg
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            WanComposeApp()
        }
    }
}

interface WanComposeDestination {

    val route: String

}


@Composable
fun WanComposeApp() {

    var currentScreen: WanComposeDestination by remember {
        mutableStateOf(HomeScreen)
    }

    val navController = rememberNavController()
    val uiController = rememberSystemUiController()
    LaunchedEffect(uiController) {
        uiController.setStatusBarColor(Color.Transparent)
    }

    NavHost(
        navController = navController,
        startDestination = HomeScreen.route,
    ) {

        composable(HomeScreen.route) {
            HomeScreen.Screen(
                Modifier.fillMaxSize(),
                toWebLink = {
                    navController.navigateToWebViewScreen(it)
                }
            )
        }

        composable(
            route = WebViewScreen.routeWithArgs,
            arguments = WebViewScreen.arguments
        ) { entry ->
            WebViewScreen.Screen(
                Modifier.fillMaxSize(),
                rememberWebViewState(url = entry.arguments?.getString(urlArg) ?: ""),
                navController = navController
            )
        }

    }

}
