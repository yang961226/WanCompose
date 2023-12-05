package com.sundayting.wancompose.page.homescreen.mine.share

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListSingleBean
import com.sundayting.wancompose.theme.WanColors

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
        onClickArticle: (String) -> Unit,
    ) {
        MyShareArticleContent(
            modifier = modifier,
            state = viewModel.state,
            onClickBackButton = onClickBackButton,
            onLoadMore = viewModel::loadMore,
            onClickArticle = onClickArticle,
            onCollect = { id: Long, isCollect: Boolean ->
                viewModel.collectArticle(id, isCollect)
            }
        )
    }

    @Composable
    fun MyShareArticleContent(
        modifier: Modifier = Modifier,
        state: MyShareArticleViewModel.MyShareArticleState,
        onClickBackButton: () -> Unit,
        onClickArticle: (String) -> Unit,
        onCollect: ((id: Long, isCollect: Boolean) -> Unit)? = null,
        onLoadMore: () -> Unit,
    ) {

        TitleBarWithContent(
            modifier = modifier,
            titleBarContent = {
                TitleBarWithBackButtonContent(onClickBackButton = onClickBackButton) {
                    Text(
                        stringResource(id = R.string.my_share),
                        style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {
            val lazyListState = rememberLazyListState()
            lazyListState.onBottomReached {
                onLoadMore()
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = lazyListState
            ) {
                items(state.articleList, key = { it.id }) {
                    ArticleListSingleBean(
                        modifier = Modifier
                            .animateItemPlacement()
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = rememberRipple()
                            ) {
                                onClickArticle(it.link)
                            }
                            .padding(10.dp),
                        bean = it,
                        onCollect = onCollect
                    )
                    Divider(Modifier.fillMaxWidth())
                }
                if (state.isLoadingMore) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = WanColors.TopColor
                            )
                        }
                    }
                }
            }
        }

    }
}