package com.sundayting.wancompose.page.homescreen.mine.share

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent

object MyShareArticleScreen : WanComposeDestination {
    override val route: String
        get() = "我的分享页面"

    fun NavController.navigateToMyShareScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: MyShareArticleViewModel = hiltViewModel(),
        onClickBackButton: () -> Unit,
    ) {
        MyShareArticleContent(
            modifier = modifier,
            state = viewModel.state,
            onClickBackButton = onClickBackButton,
            onLoadMore = viewModel::loadMore
        )
    }

    @Composable
    fun MyShareArticleContent(
        modifier: Modifier = Modifier,
        state: MyShareArticleViewModel.MyShareArticleState,
        onClickBackButton: () -> Unit,
        onLoadMore: () -> Unit,
    ) {
        TitleBarWithContent(
            modifier = modifier,
            titleBarContent = {
                TitleBarWithBackButtonContent(onClickBackButton = onClickBackButton) {
                    Text(
                        stringResource(id = R.string.my_collect),
                        style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {

        }
    }
}