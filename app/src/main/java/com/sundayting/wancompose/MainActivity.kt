package com.sundayting.wancompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.core.snap
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.web.rememberWebViewState
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ToastEvent
import com.sundayting.wancompose.function.UserLoginFunction.UserEntity
import com.sundayting.wancompose.page.homescreen.HomeScreen
import com.sundayting.wancompose.page.homescreen.mine.MineScreen
import com.sundayting.wancompose.page.homescreen.mine.ui.LoginContent
import com.sundayting.wancompose.page.setting.SettingScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen.urlArg
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        handleEvent()
        setContent {
            WanComposeApp()
        }
    }
}

private fun MainActivity.handleEvent() {
    lifecycleScope.launch {
        EventManager.eventFlow.filterIsInstance<ToastEvent>().collect {
            Toast.makeText(
                this@handleEvent,
                it.content,
                if (it.isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
            ).show()
        }
    }
}

interface WanComposeDestination {

    val route: String

}

val LocalLoginUser = staticCompositionLocalOf<UserEntity?> { null }


@Composable
fun WanComposeApp(
    viewModel: WanViewModel = viewModel(),
) {

    val loginUser by viewModel.curLoginUserFlow.collectAsState(null)
    val isLogin by remember {
        derivedStateOf { loginUser != null }
    }

    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val bottomSheetPagerState = rememberPagerState()
    rememberSystemUiController().apply {
        setStatusBarColor(Color.Transparent)
    }
    val context = LocalContext.current
    LaunchedEffect(isLogin, context) {
        if (isLogin) {
            Toast.makeText(
                context,
                context.getString(R.string.welcome_back_tip, loginUser?.nick),
                Toast.LENGTH_LONG
            ).show()
            modalSheetState.hide()
        }
    }

    CompositionLocalProvider(
        LocalLoginUser provides loginUser,
    ) {
        ModalBottomSheetLayout(
            sheetState = modalSheetState,
            sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            sheetContent = {
                BackHandler(enabled = modalSheetState.isVisible) {
                    coroutineScope.launch {
                        modalSheetState.hide()
                    }
                }
                LoginContent(
                    Modifier.fillMaxWidth(),
                    viewModel.loginOrRegisterState,
                    onClickLogin = { username, password ->
                        viewModel.login(username, password)
                    },
                    onClickRegister = { username: String, password: String, passwordAgain: String ->
                        viewModel.register(username, password, passwordAgain)
                    }
                )
            },
        ) {
            val navController = rememberAnimatedNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            //在主页
            val isInMainPage by remember {
                derivedStateOf {
                    HomeScreen.pageList.any { it.page.route == navBackStackEntry?.destination?.route }
                }
            }

            val isInPageNeedLogin by remember {
                derivedStateOf {
                    navBackStackEntry?.destination?.route?.let { route ->
                        route == MineScreen.route || route == SettingScreen.route
                    } == true
                }
            }
            LaunchedEffect(isLogin, isInPageNeedLogin) {
                //如果当前不在主页而且在个人页的情况下，就会返回主页
                if (!isLogin && isInPageNeedLogin) {
                    navController.navigate(HomeScreen.HomeScreenPage.ArticleList.route) {
                        popUpTo(
                            navController.graph.findStartDestination().id
                        ) {
                            saveState = false
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            }

            Scaffold(
                modifier = Modifier
                    .navigationBarsPadding(),
                bottomBar = {
                    if (isInMainPage) {
                        HomeScreen.Navigation(
                            navController = navController,
                            onClickBottom = { bottomItem ->

                                fun toDestination(route: String) {
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }

                                if (bottomItem.page == HomeScreen.HomeScreenPage.Mine) {
                                    if (isLogin) {
                                        toDestination(bottomItem.page.route)
                                    } else {
                                        coroutineScope.launch {
                                            bottomSheetPagerState.animateScrollToPage(
                                                0,
                                                animationSpec = snap()
                                            )
                                            modalSheetState.show()
                                        }
                                    }
                                } else {
                                    toDestination(bottomItem.page.route)
                                }
                            })
                    }
                }
            ) { it ->
                AnimatedNavHost(
                    modifier = Modifier.padding(it),
                    startDestination = HomeScreen.route,
                    navController = navController,
                ) {
                    with(HomeScreen) {
                        homeNavGraph(navController)
                    }

                    composable(
                        route = SettingScreen.route,
                        enterTransition = { slideInHorizontally { width -> width } },
                        exitTransition = { slideOutHorizontally { width -> width } }
                    ) {
                        SettingScreen.Screen(
                            Modifier.fillMaxSize(),
                            navController = navController
                        )
                    }

                    composable(
                        route = WebViewScreen.routeWithArgs,
                        arguments = WebViewScreen.arguments,
                        enterTransition = { slideInHorizontally { width -> width } },
                        exitTransition = { slideOutHorizontally { width -> width } }
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

}
