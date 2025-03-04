package com.sundayting.wancompose

import android.Manifest
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.LocalEventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.ToastEvent
import com.sundayting.wancompose.common.helper.DarkModeHelper
import com.sundayting.wancompose.common.helper.LocalDarkMode
import com.sundayting.wancompose.common.helper.LocalDarkModeFollowSystem
import com.sundayting.wancompose.common.helper.LocalDarkModeHelper
import com.sundayting.wancompose.common.helper.LocalSetToDarkMode
import com.sundayting.wancompose.common.helper.LocalVibratorHelper
import com.sundayting.wancompose.common.helper.VibratorHelper
import com.sundayting.wancompose.function.UserLoginFunction.UserEntity
import com.sundayting.wancompose.page.aboutme.AboutMe
import com.sundayting.wancompose.page.homescreen.HomeScreen
import com.sundayting.wancompose.page.homescreen.mine.point.PointScreen
import com.sundayting.wancompose.page.homescreen.mine.share.MyCollectedArticle
import com.sundayting.wancompose.page.homescreen.mine.ui.LoginContent
import com.sundayting.wancompose.page.myshare.MyShareScreen
import com.sundayting.wancompose.page.scan.ScanScreen
import com.sundayting.wancompose.page.search.SearchScreen
import com.sundayting.wancompose.page.setting.SettingScreen
import com.sundayting.wancompose.page.share.ShareScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen.navigateToWebViewScreen
import com.sundayting.wancompose.service.WanService
import com.sundayting.wancompose.theme.WanTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var eventManager: EventManager

    @Inject
    lateinit var vibratorHelper: VibratorHelper

    @Inject
    lateinit var darkModeHelper: DarkModeHelper

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startForegroundService(Intent(this@MainActivity, WanService::class.java))
            }
        }

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onCreate(owner: LifecycleOwner) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    startForegroundService(Intent(this@MainActivity, WanService::class.java))
                }

                enableEdgeToEdge()
                setContent {
                    val setToDarkMode by darkModeHelper.darkModeSettingFlow.collectAsState()
                    val darkModeFollowSystem by darkModeHelper.darkModeFollowSystemFlow.collectAsState()
                    val isDarkMode =
                        if (darkModeFollowSystem) isSystemInDarkTheme() else setToDarkMode
                    CompositionLocalProvider(
                        LocalEventManager provides eventManager,
                        LocalVibratorHelper provides vibratorHelper,
                        LocalDarkMode provides isDarkMode,
                        LocalDarkModeHelper provides darkModeHelper,
                        LocalDarkModeFollowSystem provides darkModeFollowSystem,
                        LocalSetToDarkMode provides setToDarkMode
                    ) {
                        WanTheme {
                            WanComposeApp()
                        }
                    }
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

    val scope = rememberCoroutineScope()

    var isShowBottomSheet by remember { mutableStateOf(false) }

    val modalSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            it != SheetValue.PartiallyExpanded
        }
    )

    LaunchedEffect(Unit) {
        snapshotFlow { isLogin }.collectLatest {
            if (it && isShowBottomSheet) {
                scope.launch {
                    modalSheetState.hide()
                }.invokeOnCompletion {
                    isShowBottomSheet = false
                }
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
                    isShowBottomSheet = true
                }
        }

        val navController = rememberNavController()

        Column(Modifier.fillMaxSize()) {
            NavHost(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
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
                composable(
                    route = HomeScreen.route
                ) {
                    HomeScreen.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController
                    )
                }

                composable(PointScreen.route) {
                    PointScreen.Screen(
                        modifier = Modifier.fillMaxSize(),
                        onClickBackButton = navController::popBackStack
                    )
                }
                composable(MyCollectedArticle.route) {
                    MyCollectedArticle.Screen(
                        modifier = Modifier.fillMaxSize(),
                        onClickBackButton = navController::popBackStack,
                        onClickArticle = {

                            navController.navigateToWebViewScreen(it)
                        }
                    )
                }
                composable(AboutMe.route) {
                    AboutMe.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController
                    )
                }

                composable(MyShareScreen.route) {
                    MyShareScreen.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController,
                    )
                }

                composable(ShareScreen.route) {
                    ShareScreen.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController,
                    )
                }

                composable(SettingScreen.route) {
                    SettingScreen.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController
                    )
                }

                composable(SearchScreen.route) {
                    SearchScreen.Screen(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController
                    )
                }

                composable(
                    route = ScanScreen.route,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Up,
                            animationSpec = tween(
                                durationMillis = 300
                            )
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Down,
                            animationSpec = tween(
                                durationMillis = 300
                            )
                        )
                    },
                ) {
                    ScanScreen.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController
                    )
                }

                composable(
                    route = WebViewScreen.routeWithArgs,
                    arguments = WebViewScreen.arguments,
                ) {
                    WebViewScreen.Screen(
                        Modifier.fillMaxSize(),
                        navController = navController
                    )
                }
            }
        }

        if (isShowBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.consumeWindowInsets(WindowInsets.navigationBars),
                onDismissRequest = {
                    scope.launch {
                        modalSheetState.hide()
                    }.invokeOnCompletion {
                        isShowBottomSheet = false
                    }
                },
                sheetState = modalSheetState,
                dragHandle = null
            ) {
                LoginContent(
                    loginOrRegisterState = viewModel.loginOrRegisterState,
                    onClickLogin = viewModel::login,
                    onClickRegister = viewModel::register,
                )
            }
        }
    }

}
