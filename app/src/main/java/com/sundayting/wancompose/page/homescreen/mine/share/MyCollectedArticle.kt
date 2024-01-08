package com.sundayting.wancompose.page.homescreen.mine.share

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.dialog.ConfirmDialog
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListSingleBean
import com.sundayting.wancompose.theme.WanColors

object MyCollectedArticle : WanComposeDestination {
    override val route: String
        get() = "我的收藏页面"

    fun NavController.navigateToMyCollectedScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: MyCollectedArticleViewModel = hiltViewModel(),
        onClickBackButton: () -> Unit,
        onClickArticle: (articleUiBean: ArticleList.ArticleUiBean) -> Unit,
    ) {
        MyCollectedArticleContent(
            modifier = modifier,
            state = viewModel.state,
            onClickBackButton = onClickBackButton,
            onLoadMore = viewModel::loadMore,
            onClickArticle = onClickArticle,
            onUnCollected = {
                viewModel.unCollectArticle(it)
            }
        )
    }

    @Composable
    fun MyCollectedArticleContent(
        modifier: Modifier = Modifier,
        state: MyCollectedArticleViewModel.MyCollectedArticleState,
        onClickBackButton: () -> Unit,
        onClickArticle: (articleUiBean: ArticleList.ArticleUiBean) -> Unit,
        onUnCollected: (articleUiBean: ArticleList.ArticleUiBean) -> Unit,
        onLoadMore: () -> Unit,
    ) {

        var confirmUnCollectArticle by remember {
            mutableStateOf<ArticleList.ArticleUiBean?>(null)
        }
        confirmUnCollectArticle?.let { article ->
            ConfirmDialog(
                onConfirm = {
                    onUnCollected(article)
                    confirmUnCollectArticle = null
                },
                onDismiss = { confirmUnCollectArticle = null }
            ) {
                Text(
                    text = stringResource(
                        id = R.string.article_uncollect_confirm,
                        article.title
                    ),
                    style = TextStyle(fontSize = 16.sp, color = Color.Black),
                    textAlign = TextAlign.Center
                )
            }
        }

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
            val lazyListState = rememberLazyListState()
            lazyListState.onBottomReached {
                onLoadMore()
            }
            val showLoading by remember {
                derivedStateOf { state.articleList.isEmpty() && state.isLoadingMore }
            }
            Crossfade(targetState = showLoading, label = "") { isShowLoading ->
                if (isShowLoading) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(50.dp),
                            color = WanColors.TopColor
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        state = lazyListState
                    ) {
                        items(state.articleList, key = { it.id }) {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .animateItemPlacement()
                            ) {
                                ArticleListSingleBean(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple()
                                        ) {
                                            onClickArticle(it)
                                        }
                                        .padding(10.dp),
                                    bean = it,
                                    onCollect = {
                                        confirmUnCollectArticle = it
                                    }
                                )
                                Divider(Modifier.fillMaxWidth())
                            }
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

    }
}