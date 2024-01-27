package com.sundayting.wancompose.page.myshare

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListSingleBean
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme

object MyShareScreen : WanComposeDestination {

    override val route: String
        get() = "我的分享"


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

            Content(state = viewModel.state)

        }

    }

    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        state: MyShareViewModel.MyShareArticleUiState,
        onLoadMore: () -> Unit = {},
    ) {

        val lazyListState = rememberLazyListState()
        lazyListState.onBottomReached {
            onLoadMore()
        }
        Crossfade(
            modifier = modifier.background(WanTheme.colors.level1BackgroundColor),
            targetState = state.isLoading,
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
                LazyColumn(Modifier.fillMaxSize()) {
                    items(state.articleList, key = { it.id }) {
                        Column(
                            Modifier
                                .background(WanTheme.colors.level2BackgroundColor)
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
//                                        onClickArticle(it)
                                    }
                                    .padding(10.dp),
                                bean = it,
                                onCollect = {
//                                    confirmUnCollectArticle = it
                                }
                            )
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