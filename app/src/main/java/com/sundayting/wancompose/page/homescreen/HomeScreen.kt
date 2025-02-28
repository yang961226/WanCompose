package com.sundayting.wancompose.page.homescreen

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.LocalLoginUser
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.event.LocalEventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.loading.LocalLoadingBoxIsLoading
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ArticlePageState
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListContent
import com.sundayting.wancompose.page.homescreen.mine.MineScreen
import com.sundayting.wancompose.page.scan.ScanScreen.navigateToScanScreen
import com.sundayting.wancompose.page.search.SearchScreen.navigateToSearchScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

object HomeScreen : WanComposeDestination {

    override val route: String
        get() = "首页"

    interface HomeScreenPage : WanComposeDestination {

        data class BottomItem(
            @DrawableRes val resId: Int,
            @StringRes val titleId: Int,
        )

    }

    val pageList = listOf(
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_home,
            titleId = R.string.bottom_tab_home,
        ),
        HomeScreenPage.BottomItem(
            resId = R.drawable.ic_mine,
            titleId = R.string.bottom_tab_mine,
        ),
    )

    @Composable
    fun WanBottomNavigation(
        selectedIndex: Int,
        onClickBottom: (Int) -> Unit = {},
    ) {
        Column {
            HorizontalDivider(color = WanTheme.colors.level3BackgroundColor)
            Row(
                Modifier
                    .background(WanTheme.colors.level2BackgroundColor)
                    .navigationBarsPadding()
                    .height(60.dp)
            ) {
                pageList.forEachIndexed { index, bottomItem ->
                    NavigationItem(
                        Modifier
                            .fillMaxWidth()
                            .weight(1f, false),
                        bean = bottomItem,
                        isSelected = selectedIndex == index,
                        onClickItem = {
                            onClickBottom(index)
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
                    indication = null,
                    enabled = !isSelected
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

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
        viewModel: HomeViewModel = hiltViewModel(),
    ) {

        val pagerState = rememberPagerState {
            pageList.size
        }

        val scope = rememberCoroutineScope()

        if (pagerState.currentPage != 0) {
            BackHandler {
                scope.launch {
                    pagerState.scrollToPage(0)
                }
            }
        }

        val eventManager = LocalEventManager.current
        LaunchedEffect(Unit) {
            eventManager.eventFlow.filterIsInstance<ShowLoginPageEvent>()
        }

        Column(
            modifier.fillMaxSize()
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f, false),
                userScrollEnabled = false
            ) {
                when (it) {
                    0 -> {
                        val articleState = viewModel.articlePageState
                        CompositionLocalProvider(
                            LocalLoadingBoxIsLoading provides (articleState.refreshing && articleState.articleList.isEmpty())
                        ) {
                            HomeArticleListContent(
                                state = articleState,
                                onRefresh = viewModel::refreshArticle,
                                onLoadMore = viewModel::loadMoreArticle,
                                collectOrUnCollectArticle = viewModel::collectOrUnCollectArticle,
                                navController = navController
                            )
                        }
                    }

                    1 -> {
                        MineScreen.Screen(
                            modifier = Modifier.fillMaxSize(),
                            userEntity = LocalLoginUser.current,
                            navController = navController
                        )
                    }
                }
            }
            WanBottomNavigation(
                selectedIndex = pagerState.currentPage,
                onClickBottom = {
                    scope.launch {
                        pagerState.scrollToPage(it)
                    }
                }
            )
        }


    }

    @Composable
    private fun HomeArticleListContent(
        modifier: Modifier = Modifier,
        state: ArticlePageState,
        navController: NavController,
        onRefresh: () -> Unit,
        onLoadMore: () -> Unit,
        collectOrUnCollectArticle: (ArticleList.ArticleUiBean) -> Unit,
    ) {
        TitleBarWithContent(
            modifier.fillMaxSize(),
            titleBarContent = {
                Image(
                    painter = painterResource(id = R.drawable.ic_scan),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 15.dp)
                        .size(20.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            navController.navigateToScanScreen()
                        },
                    contentScale = ContentScale.Fit,
                    colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
                )
                Text(
                    stringResource(id = R.string.bottom_tab_home),
                    style = TitleTextStyle,
                    modifier = Modifier.align(Alignment.Center)
                )
                Image(
                    painterResource(id = R.drawable.ic_search),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .size(25.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { navController.navigateToSearchScreen() }
                        .align(Alignment.CenterEnd),
                    colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
                )
            }
        ) {
            PullToRefreshBox(
                modifier = Modifier
                    .fillMaxSize(),
                isRefreshing = state.refreshing,
                onRefresh = onRefresh
            ) {
                val lazyListState = rememberLazyListState()
                lazyListState.onBottomReached {
                    onLoadMore()
                }
                ArticleListContent(
                    modifier = Modifier.matchParentSize(),
                    articleState = state,
                    lazyListState = lazyListState,
                    onClickArticle = {
                        with(WebViewScreen) {
                            navController.navigateToWebViewScreen(it)
                        }
                    },
                    onClickBanner = {
                        with(WebViewScreen) {
                            navController.navigateToWebViewScreen(it)
                        }
                    },
                    onCollect = collectOrUnCollectArticle
                )
            }
        }

    }

}