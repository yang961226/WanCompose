package com.sundayting.wancompose.page.homescreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.MineGraph
import com.sundayting.wancompose.page.homescreen.mine.MineScreen
import com.sundayting.wancompose.page.scan.ScanScreen
import com.sundayting.wancompose.page.search.SearchScreen
import com.sundayting.wancompose.theme.WanTheme

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
//        HomeScreenPage.BottomItem(
//            resId = R.drawable.ic_example,
//            titleId = R.string.bottom_tab_example,
//            page = ExampleWidget
//        ),
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
        Column {
            HorizontalDivider(color = WanTheme.colors.level3BackgroundColor)
            Row(
                Modifier
                    .background(WanTheme.colors.level2BackgroundColor)
                    .navigationBarsPadding()
                    .height(60.dp)
            ) {
                pageList.forEach { bottomItem ->
                    NavigationItem(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, false),
                        bean = bottomItem,
                        isSelected = curRoute == bottomItem.page.route,
                        onClickItem = {
                            onClickBottom(bottomItem)
                        }
                    )
                }
            }
        }

    }

    @Composable
    private fun NavigationItem(
        modifier: Modifier = Modifier,
        bean: HomeScreenPage.BottomItem,
        isSelected: Boolean,
        onClickItem: () -> Unit,
    ) {

        ConstraintLayout(
            modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClickItem()
                }
        ) {

            val (
                imageContent,
                textContent,
            ) = createRefs()

            val color =
                if (isSelected) WanTheme.colors.level1TextColor else WanTheme.colors.level3TextColor

            Box(
                modifier = Modifier
                    .constrainAs(imageContent) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top, 10.dp)
                        width = Dimension.value(24.dp)
                        height = Dimension.value(24.dp)
                    },
            ) {
                Image(
                    painter = painterResource(bean.resId),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(color)
                )
            }


            Text(
                modifier = Modifier.constrainAs(textContent) {
                    top.linkTo(imageContent.bottom, 2.5.dp)
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom, 10.dp)
                },
                text = stringResource(id = bean.titleId),
                style = WanTheme.typography.h8,
                color = color,
                maxLines = 1
            )
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

    private fun isScanTransition(startRoute: String?, targetRout: String?): Boolean {
        return startRoute == ScanScreen.route || targetRout == ScanScreen.route
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
            } else if (isScanTransition(
                    initialState.destination.route,
                    targetState.destination.route
                )
            ) {
                fadeOut(tween(300))
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
            } else if (isScanTransition(
                    initialState.destination.route,
                    targetState.destination.route
                )
            ) {
                fadeIn(tween(300))
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
                    navController = navController,
                )
            }

            composable(SearchScreen.route) {
                SearchScreen.Screen(
                    modifier = Modifier.fillMaxSize(),
                    navController = navController
                )
            }

            with(MineGraph) {
                graph(navController)
            }
        }
    }

}