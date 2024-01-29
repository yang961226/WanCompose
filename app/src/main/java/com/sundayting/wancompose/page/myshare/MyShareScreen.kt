package com.sundayting.wancompose.page.myshare

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateTo
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.dialog.NormalConfirmDialog
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.scrolldelete.DragValue
import com.sundayting.wancompose.common.ui.scrolldelete.ScrollToDelete
import com.sundayting.wancompose.common.ui.scrolldelete.rememberScrollToDeleteState
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListSingleBean
import com.sundayting.wancompose.page.webscreen.WebViewScreen.navigateToWebViewScreen
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch

object MyShareScreen : WanComposeDestination {

    override val route: String
        get() = "我的分享"

    fun NavController.navigateToMyShareScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }


    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
        viewModel: MyShareViewModel = hiltViewModel(),
    ) {

        TitleBarWithContent(
            modifier,
            titleBarContent = {
                TitleBarWithBackButtonContent(onClickBackButton = {
                    navController.popBackStack()
                }) {
                    Text(
                        stringResource(id = R.string.my_share),
                        style = TitleTextStyle,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {

            Content(
                state = viewModel.state,
                onLoadMore = {
                    viewModel.loadMore()
                },
                onClickArticle = {
                    navController.navigateToWebViewScreen(it)
                },
                onCollectOrUnCollect = { bean, tryCollect ->
                    viewModel.collectOrUnCollect(bean, tryCollect)
                },
                onDeleteArticle = {
                    viewModel.deleteSharedArticle(it)
                }
            )

        }

    }

    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        state: MyShareViewModel.MyShareArticleUiState,
        onLoadMore: () -> Unit = {},
        onClickArticle: (ArticleList.ArticleUiBean) -> Unit = {},
        onDeleteArticle: (ArticleList.ArticleUiBean) -> Unit = {},
        onCollectOrUnCollect: (ArticleList.ArticleUiBean, tryCollect: Boolean) -> Unit = { _, _ -> },
    ) {

        var confirmUnCollectArticle by remember {
            mutableStateOf<ArticleList.ArticleUiBean?>(null)
        }
        confirmUnCollectArticle?.let { article ->

            NormalConfirmDialog(
                mainContent = stringResource(
                    id = R.string.delete_tip,
                    article.title
                ),
                onConfirm = {
                    onCollectOrUnCollect(article, false)
                    confirmUnCollectArticle = null
                },
                onDismiss = {
                    confirmUnCollectArticle = null
                }
            )
        }

        var deleteArticle by remember {
            mutableStateOf<ArticleList.ArticleUiBean?>(null)
        }

        deleteArticle?.let { article ->
            NormalConfirmDialog(
                mainContent = stringResource(
                    id = R.string.article_uncollect_confirm,
                    article.title
                ),
                onConfirm = {
                    onDeleteArticle(article)
                    deleteArticle = null
                },
                onDismiss = {
                    deleteArticle = null
                }
            )
        }

        val lazyListState = rememberLazyListState()
        lazyListState.onBottomReached {
            onLoadMore()
        }

        val showLoading by remember {
            derivedStateOf { state.articleList.isEmpty() && state.isLoadingMore }
        }

        val isListScroll = lazyListState.isScrollInProgress

        Crossfade(
            modifier = modifier.background(WanTheme.colors.level1BackgroundColor),
            targetState = showLoading,
            label = ""
        ) { isLoading ->
            if (isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = WanTheme.colors.primaryColor
                    )
                }
            } else {
                var isDraggableId by rememberSaveable { mutableLongStateOf(0L) }
                LazyColumn(
                    Modifier.fillMaxSize(),
                    state = lazyListState
                ) {
                    items(state.articleList, key = { it.id }) { articleBean ->
                        val anchoredDraggableState = rememberScrollToDeleteState()
                        val scope = rememberCoroutineScope()
                        LaunchedEffect(anchoredDraggableState) {
                            launch {
                                snapshotFlow { isListScroll }.collect {
                                    if (it) {
                                        scope.launch {
                                            anchoredDraggableState.animateTo(DragValue.IDLE)
                                        }
                                    }
                                }
                            }
                            launch {
                                snapshotFlow { anchoredDraggableState.targetValue }.collect {
                                    if (it == DragValue.SHOW) {
                                        isDraggableId = articleBean.id
                                    }
                                }
                            }
                            launch {
                                snapshotFlow { isDraggableId }.drop(1).collect {
                                    if (it != articleBean.id) {
                                        scope.launch {
                                            anchoredDraggableState.animateTo(DragValue.IDLE)
                                        }
                                    }
                                }
                            }

                        }
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .animateItemPlacement()
                        ) {
                            ScrollToDelete(
                                Modifier.fillMaxWidth(),
                                onClickDelete = {
                                    deleteArticle = articleBean
                                },
                                state = anchoredDraggableState
                            ) {
                                ArticleListSingleBean(
                                    modifier = Modifier
                                        .background(WanTheme.colors.level2BackgroundColor)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple()
                                        ) {
                                            onClickArticle(articleBean)
                                        }
                                        .padding(10.dp)
                                        .fillMaxWidth(),
                                    bean = articleBean,
                                    onCollect = {
                                        if (it.isCollect) {
                                            confirmUnCollectArticle = it
                                        } else {
                                            onCollectOrUnCollect(it, true)
                                        }
                                    }
                                )
                            }

                            Divider(
                                Modifier.fillMaxWidth(),
                                color = WanTheme.colors.level4BackgroundColor
                            )
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
                                    color = WanTheme.colors.primaryColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}