package com.sundayting.wancompose.page.homescreen.mine

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.sundayting.wancompose.LocalLoginUser
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.page.homescreen.mine.point.PointScreen
import com.sundayting.wancompose.page.homescreen.mine.share.MyCollectedArticle
import com.sundayting.wancompose.page.webscreen.WebViewScreen.navigateToWebViewScreen

object MineGraph : WanComposeDestination {
    override val route: String
        get() = "「我的」导航图"

    fun NavGraphBuilder.graph(
        navController: NavHostController,
    ) {
        navigation(
            route = MineGraph.route,
            startDestination = MineScreen.route
        ) {
            composable(MineScreen.route) {
                MineScreen.Screen(
                    modifier = Modifier.fillMaxSize(),
                    userEntity = LocalLoginUser.current,
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
        }
    }
}