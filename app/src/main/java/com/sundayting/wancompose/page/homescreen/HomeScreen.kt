package com.sundayting.wancompose.page.homescreen

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.sundayting.wancompose.LocalLoginUser
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.MineScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen.navigateToWebViewScreen

object HomeScreen : WanComposeDestination {

    override val route: String
        get() = "首页"

    sealed class HomeScreenPage(
        val route: String,
    ) {

        object ArticleList : HomeScreenPage("文章列表")
//        object System : HomeScreenPage("体系")
        object Mine : HomeScreenPage("我的")


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
            page = HomeScreenPage.ArticleList
        ),
//        HomeScreenPage.BottomItem(
//            resId = R.drawable.ic_system,
//            titleId = R.string.bottom_tab_system,
//            page = HomeScreenPage.System
//        ),
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_mine,
            titleId = R.string.bottom_tab_mine,
            page = HomeScreenPage.Mine
        ),
    )

    @Composable
    fun Navigation(
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

    fun NavGraphBuilder.homeNavGraph(
        navController: NavHostController,
    ) {
        navigation(
            route = HomeScreen.route,
            startDestination = HomeScreenPage.ArticleList.route,
            enterTransition = {
                if (pageList.any { it.page.route == initialState.destination.route }) {
                    EnterTransition.None
                } else {
                    slideInHorizontally { -it }
                }
            },
            exitTransition = {
                if (pageList.any { it.page.route == targetState.destination.route }) {
                    ExitTransition.None
                } else {
                    slideOutHorizontally { -it }
                }
            }
        ) {
            composable(HomeScreenPage.ArticleList.route) {
                ArticleList.Screen(
                    modifier = Modifier.fillMaxSize(),
                    toWebLink = {
                        navController.navigateToWebViewScreen(it)
                    }
                )
            }
//            composable(HomeScreenPage.System.route) {
//                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Text("我是2")
//                }
//            }
            composable(HomeScreenPage.Mine.route) {
                MineScreen.Screen(
                    modifier = Modifier.fillMaxSize(),
                    userEntity = LocalLoginUser.current
                )
            }
        }
    }

}