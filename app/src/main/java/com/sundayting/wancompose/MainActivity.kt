package com.sundayting.wancompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.web.rememberWebViewState
import com.sundayting.wancompose.homescreen.HomeScreen
import com.sundayting.wancompose.homescreen.minescreen.ui.LoginContent
import com.sundayting.wancompose.web.WebViewScreen
import com.sundayting.wancompose.web.WebViewScreen.urlArg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

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
fun WanComposeApp(
    viewModel: WanViewModel = viewModel(),
) {

    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val bottomSheetPagerState = rememberPagerState()
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            LoginContent(
                Modifier.fillMaxWidth(),
                viewModel.loginOrRegisterState,
                onClickLogin = { username, password ->
                    viewModel.login(username, password)
                }
            )
        },
    ) {
        val navController = rememberNavController()
        val uiController = rememberSystemUiController()
        LaunchedEffect(uiController) {
            uiController.setStatusBarColor(Color.Transparent)
        }

        Scaffold(
            modifier = Modifier
                .navigationBarsPadding(),
            bottomBar = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                if (currentDestination?.route?.let { curRoute ->
                        HomeScreen.pageList.any { it.page.route == curRoute }
                    } == true) {
                    HomeScreen.Navigation(
                        navController = navController,
                        onClickBottom = { bottomItem ->
                            if (bottomItem.page == HomeScreen.HomeScreenPage.Mine) {
                                coroutineScope.launch {
                                    bottomSheetPagerState.animateScrollToPage(
                                        0,
                                        animationSpec = snap()
                                    )
                                    modalSheetState.show()
                                }
                            } else {
                                navController.navigate(bottomItem.page.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }

                        })
                }
            }
        ) {
            NavHost(
                modifier = Modifier.padding(it),
                startDestination = HomeScreen.route,
                navController = navController
            ) {
                with(HomeScreen) {
                    homeNavGraph(navController)
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

    }
}
