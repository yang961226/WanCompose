package com.sundayting.wancompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sundayting.wancompose.homescreen.HomeScreen
import com.sundayting.wancompose.homescreen.minescreen.ui.LoginContent
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

    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    ModalBottomSheetLayout(
        sheetState = modalSheetState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        sheetContent = {
            LoginContent(Modifier.fillMaxWidth())
        }
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
                        HomeScreen.pageList.any { it.route == curRoute }
                    } == true) {
                    BottomNavigation(backgroundColor = Color(0xFF5380ec)) {
                        HomeScreen.bottomItemList.forEach { bottomItem ->
                            BottomNavigationItem(
                                unselectedContentColor = Color.White,
                                selected = false,
                                onClick = {
                                    navController.navigate(bottomItem.page.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = bottomItem.resId),
                                        contentDescription = null,
                                        modifier = Modifier.size(25.dp)
                                    )
                                },
                                label = {
                                    Text(stringResource(id = bottomItem.titleId))
                                }
                            )
                        }
                    }
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

                composable("otherPage") {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("其他页面", modifier = Modifier.clickable {
                            navController.navigate("otherPage")
                        })
                    }
                }
            }
        }

//        NavHost(
//            navController = navController,
//            startDestination = HomeScreen.route,
//        ) {
//            composable(route = HomeScreen.route) {
//                HomeScreen.Screen(
//                    modifier = Modifier.fillMaxSize(),
//                    navController = navController
//                )
//            }
//
//            composable(
//                route = WebViewScreen.routeWithArgs,
//                arguments = WebViewScreen.arguments
//            ) { entry ->
//                WebViewScreen.Screen(
//                    Modifier.fillMaxSize(),
//                    rememberWebViewState(url = entry.arguments?.getString(urlArg) ?: ""),
//                    navController = navController
//                )
//            }
//
//        }
    }
}
