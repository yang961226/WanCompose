package com.sundayting.wancompose.page.search

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.textfield.WanTextField
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListSingleBean
import com.sundayting.wancompose.page.search.SearchScreen.Content
import com.sundayting.wancompose.page.search.SearchViewModel.SearchUiState.SearchPageType.ResultPage
import com.sundayting.wancompose.page.search.SearchViewModel.SearchUiState.SearchPageType.TipsPage
import com.sundayting.wancompose.theme.WanTheme

object SearchScreen : WanComposeDestination {

    override val route: String = "搜索页面"

    fun NavController.navigateToSearchScreen() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
        viewModel: SearchViewModel = hiltViewModel(),
    ) {
        Content(
            modifier = modifier,
            state = viewModel.uiState,
            onClickBack = {
                navController.popBackStack()
            },
            onInputChanged = viewModel::onSearchInputChanged,
            onClickSearchItem = {
                viewModel.onSearch(it)
            },
            onClickSearch = {
                viewModel.onSearch()
            },
            onClickClear = {
                viewModel.clearHistory()
            },
            onLoadMore = {
                viewModel.loadMore()
            }
        )
    }

    @Composable
    fun Content(
        modifier: Modifier = Modifier,
        state: SearchViewModel.SearchUiState,
        onInputChanged: (TextFieldValue) -> Unit = {},
        onClickSearchItem: (String) -> Unit = {},
        onClickSearch: () -> Unit = {},
        onClickBack: () -> Unit = {},
        onClickClear: () -> Unit = {},
        onLoadMore: () -> Unit = {},
    ) {

        BackHandler(enabled = state.searchPageType == ResultPage) {
            state.searchPageType = TipsPage
        }

        TitleBarWithContent(
            modifier = modifier,
            titleBarContent = {
                TitleBarWithBackButtonContent(onClickBackButton = onClickBack) {
                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .fillMaxSize()
                            .padding(horizontal = 60.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(WanTheme.colors.level4BackgroundColor),
                        contentAlignment = Alignment.Center
                    ) {
                        WanTextField(
                            value = state.searchInputString,
                            onValueChange = onInputChanged,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 15.dp),
                            placeHolder = {
                                Text(
                                    text = stringResource(id = R.string.search_hint),
                                    style = WanTheme.typography.h7.copy(
                                        color = WanTheme.colors.level3TextColor
                                    ),
                                    maxLines = 1,
                                    overflow = TextOverflow.Visible
                                )
                            },
                            textStyle = WanTheme.typography.h7.copy(
                                color = WanTheme.colors.level1TextColor
                            ),
                            singleLine = true
                        )
                    }
                    Image(
                        painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 20.dp)
                            .size(25.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onClickSearch() }
                            .align(Alignment.CenterEnd),
                        colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
                    )
                }
            }
        ) {
            Crossfade(
                targetState = state.searchPageType, modifier = Modifier
                    .fillMaxSize()
                    .background(WanTheme.colors.level1BackgroundColor), label = ""
            ) { type ->
                when (type) {
                    TipsPage -> {
                        TipsContent(
                            Modifier
                                .fillMaxSize()
                                .padding(20.dp),
                            state = state,
                            onClickSearchItem = onClickSearchItem,
                            onClickClear = onClickClear
                        )
                    }

                    ResultPage -> {
                        ResultContent(
                            Modifier.fillMaxSize(),
                            state = state,
                            onLoadMore = {
                                onLoadMore()
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TipsContent(
        modifier: Modifier = Modifier,
        state: SearchViewModel.SearchUiState,
        onClickSearchItem: (String) -> Unit,
        onClickClear: () -> Unit,
    ) {
        Column(modifier) {
            Text(
                text = stringResource(id = R.string.hot_search),
                style = WanTheme.typography.h6.copy(
                    color = WanTheme.colors.primaryColor
                )
            )
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.hotSearchList.fastForEach {
                    TipsItem(
                        text = it,
                        onClick = onClickSearchItem
                    )
                }
            }
            Spacer(Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.history_search),
                    style = WanTheme.typography.h6.copy(
                        color = WanTheme.colors.primaryColor
                    )
                )
                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, false)
                )
                Text(
                    text = stringResource(id = R.string.clear),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level3TextColor
                    ),
                    modifier = Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onClickClear() }
                )
            }
            Spacer(Modifier.height(10.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                state.historySearchList.fastForEach {
                    TipsItem(
                        text = it,
                        onClick = onClickSearchItem
                    )
                }
            }

        }
    }

    @Composable
    private fun ResultContent(
        modifier: Modifier = Modifier,
        state: SearchViewModel.SearchUiState,
        onLoadMore: () -> Unit,
    ) {
        val lazyListState = rememberLazyListState()
        lazyListState.onBottomReached {
            onLoadMore()
        }
        LazyColumn(
            modifier
                .fillMaxSize(),
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
//                            onClickArticle(it)
                        }
                        .padding(10.dp),
                    bean = it,
//                    onCollect = onCollect
                )
                Divider(Modifier.fillMaxWidth(), color = WanTheme.colors.level4BackgroundColor)
            }
            if (state.isLoadingMore) {
                item(key = "加载框") {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = WanTheme.colors.tipColor
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TipsItem(
        modifier: Modifier = Modifier,
        text: String,
        onClick: (String) -> Unit,
    ) {
        Text(
            text = text,
            style = WanTheme.typography.h7.copy(
                color = WanTheme.colors.level3TextColor
            ),
            modifier = modifier
                .clip(RoundedCornerShape(50))
                .background(WanTheme.colors.level2BackgroundColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple()
                ) { onClick(text) }
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }

}

@Composable
@Preview
private fun PreviewContent() {
    Content(
        Modifier.fillMaxSize(),
        state = remember {
            SearchViewModel.SearchUiState().apply {
                hotSearchList.addAll(
                    listOf(
                        "面试", "Studio3", "动画", "自定义View", "性能优化 速度"
                    )
                )
            }
        },
        onClickBack = {

        }
    )
}