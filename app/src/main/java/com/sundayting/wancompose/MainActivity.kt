package com.sundayting.wancompose

import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.web.rememberWebViewState
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.ToastEvent
import com.sundayting.wancompose.common.event.emitToast
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
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var eventManager: EventManager

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                WindowCompat.setDecorFitsSystemWindows(window, false)
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                setContent {
                    WanComposeApp()
                }
            }
        })
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                eventManager.eventFlow.filterIsInstance<ToastEvent>().collect {
                    Toast.makeText(
                        this@MainActivity,
                        it.content,
                        if (it.isLong) Toast.LENGTH_LONG else Toast.LENGTH_SHORT
                    ).show()
                }
            }

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

    val loginUser by viewModel.curLoginUserFlow.collectAsStateWithLifecycle()
    val isLogin by remember {
        derivedStateOf { loginUser != null }
    }

    val coroutineScope = rememberCoroutineScope()

    val modalSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = { it != ModalBottomSheetValue.HalfExpanded },
        skipHalfExpanded = true
    )
    val bottomSheetPagerState = rememberPagerState { 2 }
    rememberSystemUiController().apply {
        setStatusBarColor(Color.Transparent)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { isLogin }.collect {
            if (it) {
                modalSheetState.hide()
            }
        }
    }

    CompositionLocalProvider(
        LocalLoginUser provides loginUser,
    ) {
        LaunchedEffect(Unit) {
            viewModel.eventManager
                .eventFlow
                .filterIsInstance<ShowLoginPageEvent>()
                .collect {
                    modalSheetState.show()
                }
        }
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
                    },
                    onPasswordNotRight = {
                        viewModel.eventManager.emitToast("再次输入的密码不匹配！")
                    }
                )
            },
        ) {
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()

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
            LaunchedEffect(Unit) {
                snapshotFlow {
                    isLogin to isInPageNeedLogin
                }.collect {
                    val isLoginInner = it.first
                    val isInPageNeedLoginInner = it.second
                    //如果当前不在主页而且在个人页的情况下，就会返回主页
                    if (!isLoginInner && isInPageNeedLoginInner) {
                        val startDestination = navController.graph.findStartDestination()
                        navController.navigate(startDestination.route!!) {
                            popUpTo(startDestination.id) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                }
            }

            Scaffold(
                modifier = Modifier,
                bottomBar = {
                    if (isInMainPage) {
                        HomeScreen.WanBottomNavigation(
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

                                if (bottomItem.page == MineScreen) {
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
            ) {
                NavHost(
                    modifier = Modifier.padding(it),
                    startDestination = HomeScreen.route,
                    navController = navController,
                    enterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    },
                    exitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                    },
                    popEnterTransition = {
                        slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    },
                    popExitTransition = {
                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                    }
                ) {
                    with(HomeScreen) { homeNavGraph(navController) }
                    composable(
                        route = SettingScreen.route,
                    ) {
                        SettingScreen.Screen(
                            Modifier.fillMaxSize(),
                            navController = navController
                        )
                    }

                    composable(
                        route = WebViewScreen.routeWithArgs,
                        arguments = WebViewScreen.arguments,
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
