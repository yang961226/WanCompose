package com.sundayting.wancompose.page.homescreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.page.examplewidgetscreen.ExampleWidget
import com.sundayting.wancompose.page.examplewidgetscreen.ExampleWidgetNavGraph
import com.sundayting.wancompose.page.examplewidgetscreen.nestscroll.NestScroll
import com.sundayting.wancompose.page.examplewidgetscreen.nestscroll.NestScroll.navigateToNestScroll
import com.sundayting.wancompose.page.examplewidgetscreen.pointinput.PointInput
import com.sundayting.wancompose.page.examplewidgetscreen.pointinput.PointInput.navigateToPointInput
import com.sundayting.wancompose.page.examplewidgetscreen.tantancard.TanTanSwipeCardScreen
import com.sundayting.wancompose.page.examplewidgetscreen.tantancard.TanTanSwipeCardScreen.navigateToTanTanSwipeCardScreen
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.MineGraph
import com.sundayting.wancompose.page.homescreen.mine.MineScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen.navigateToWebViewScreen

object HomeScreen : WanComposeDestination {

    override val route: String
        get() = "首页"

    interface HomeScreenPage : WanComposeDestination {

        data class BottomItem(
            @DrawableRes val resId: Int,
            @StringRes val titleId: Int,
            val page: HomeScreenPage,
        )

    }

    val pageList = listOf(
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_home,
            titleId = R.string.bottom_tab_home,
            page = ArticleList
        ),
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_example,
            titleId = R.string.bottom_tab_example,
            page = ExampleWidget
        ),
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_mine,
            titleId = R.string.bottom_tab_mine,
            page = MineScreen
        ),
    )

    @Composable
    fun WanBottomNavigation(
        navController: NavHostController,
        onClickBottom: (HomeScreenPage.BottomItem) -> Unit = {},
    ) {

        val entry by navController.currentBackStackEntryAsState()
        val curRoute by remember {
            derivedStateOf {
                entry?.destination?.route
            }
        }
        BottomNavigation(
            modifier = Modifier
                .navigationBarsPadding()
                .height(70.dp),
            backgroundColor = Color.White,
        ) {
            pageList.forEach { bottomItem ->
                BottomNavigationItem(
                    selectedContentColor = Color(0xFF5380ec),
                    unselectedContentColor = Color.Gray,
                    selected = bottomItem.page.route == curRoute,
                    onClick = {
                        onClickBottom(bottomItem)
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = bottomItem.resId),
                            contentDescription = null,
                            modifier = Modifier
                                .size(25.dp)
                                .padding(bottom = 5.dp)
                        )
                    },
                    label = {
                        Text(
                            modifier = Modifier,
                            text = stringResource(id = bottomItem.titleId)
                        )
                    }
                )
            }
        }
    }

    /**
     * 是否是首页的转移，即从首页的Tab转移到另外一个Tab
     * @param startRoute 开始的路由
     * @param targetRout 目的的路由
     */
    private fun isHomeTransition(startRoute: String?, targetRout: String?): Boolean {
        val startInHome = pageList.any { item ->
            item.page.route == startRoute
        }
        val targetInHome = pageList.any { item ->
            item.page.route == targetRout
        }
        return startInHome && targetInHome
    }

    private val DEFAULT_ENTER_TRANSITION: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        {
            if (isHomeTransition(initialState.destination.route, targetState.destination.route)) {
                EnterTransition.None
            } else {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                )
            }
        }
    private val DEFAULT_EXIT_TRANSITION: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        {
            if (isHomeTransition(initialState.destination.route, targetState.destination.route)) {
                ExitTransition.None
            } else {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                )
            }
        }

    private val DEFAULT_POP_ENTER_TRANSITION: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?) =
        {
            if (isHomeTransition(initialState.destination.route, targetState.destination.route)) {
                EnterTransition.None
            } else {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                )
            }
        }

    private val DEFAULT_POP_EXIT_TRANSITION: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?) =
        {
            if (isHomeTransition(initialState.destination.route, targetState.destination.route)) {
                ExitTransition.None
            } else {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                )
            }
        }

    fun NavGraphBuilder.homeNavGraph(
        navController: NavHostController,
    ) {
        navigation(
            route = HomeScreen.route,
            startDestination = ArticleList.route,
            enterTransition = DEFAULT_ENTER_TRANSITION,
            exitTransition = DEFAULT_EXIT_TRANSITION,
            popEnterTransition = DEFAULT_POP_ENTER_TRANSITION,
            popExitTransition = DEFAULT_POP_EXIT_TRANSITION
        ) {
            composable(ArticleList.route) {
                ArticleList.Screen(
                    modifier = Modifier.fillMaxSize(),
                    onClickArticle = {
                        navController.navigateToWebViewScreen(it)
                    },
                    onClickBanner = {
                        navController.navigateToWebViewScreen(it)
                    }
                )
            }

            navigation(
                route = ExampleWidgetNavGraph.route,
                startDestination = ExampleWidget.route
            ) {
                composable(ExampleWidget.route) {
                    ExampleWidget.Screen(
                        Modifier
                            .fillMaxSize(),
                        onClick = { bean ->
                            when (bean.name) {
                                "探探滑卡" -> navController.navigateToTanTanSwipeCardScreen()
                                "手势" -> navController.navigateToPointInput()
                                "嵌套滑动" -> navController.navigateToNestScroll()
                            }
                        }
                    )
                }

                composable(TanTanSwipeCardScreen.route) {
                    TanTanSwipeCardScreen.Screen(Modifier.fillMaxSize())
                }
                composable(PointInput.route) {
                    PointInput.Screen(Modifier.fillMaxSize(), onClickBackButton = {
                        navController.popBackStack()
                    })
                }
                composable(NestScroll.route) {
                    NestScroll.Screen(Modifier.fillMaxSize(), onClickBackButton = {
                        navController.popBackStack()
                    })
                }
            }

            with(MineGraph) {
                graph(navController)
            }
        }
    }

}