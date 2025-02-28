package com.sundayting.wancompose.page.homescreen.article.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.ui.infinitepager.InfiniteLoopHorizontalPager
import com.sundayting.wancompose.common.ui.infinitepager.currentPageInInfinitePage
import com.sundayting.wancompose.common.ui.infinitepager.rememberInfiniteLoopPagerState
import com.sundayting.wancompose.common.ui.ktx.onBottomReached
import com.sundayting.wancompose.common.ui.loading.LoadingBox
import com.sundayting.wancompose.common.ui.loading.LocalLoadingBoxIsLoading
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.HomeScreen
import com.sundayting.wancompose.page.homescreen.article.ArticleListViewModel
import com.sundayting.wancompose.page.scan.ScanScreen.navigateToScanScreen
import com.sundayting.wancompose.page.search.SearchScreen.navigateToSearchScreen
import com.sundayting.wancompose.page.webscreen.WebViewScreen.navigateToWebViewScreen
import com.sundayting.wancompose.theme.CollectColor
import com.sundayting.wancompose.theme.DarkColors
import com.sundayting.wancompose.theme.LightColors
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

object ArticleList : HomeScreen.HomeScreenPage {

    override val route: String
        get() = "文章列表"

    @Stable
    @Serializable
    data class ArticleUiBean(
        val title: String,
        val envelopePic: String? = null,
        val desc: String,
        val date: String,
        val id: Long,
        val isStick: Boolean = false,
        val isNew: Boolean = false,
        val chapter: Chapter,
        val authorOrSharedUser: AuthorOrSharedUser,
        val link: String = "",
        val isCollect: Boolean = false,
        val tags: List<Tag> = emptyList(),
    ) {

        //{"name":"本站发布","url":"/article/list/0?cid=440"}
        @Serializable
        data class Tag(
            val name: String,
            val url: String,
        )

        @Serializable
        data class Chapter(
            val chapterName: String,
            val superChapterName: String,
        )

        @Serializable
        data class AuthorOrSharedUser(
            val author: String = "",
            val sharedUser: String = "",
        )

    }

    data class BannerUiBean(
        val imgUrl: String,
        val linkUrl: String,
        val id: Long,
        val title: String,
    )

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: ArticleListViewModel = hiltViewModel(),
        navController: NavHostController,
    ) {

        CompositionLocalProvider(
            LocalLoadingBoxIsLoading provides viewModel.state.isShowLoadingBox
        ) {

            TitleBarWithContent(
                modifier,
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
                    isRefreshing = viewModel.state.refreshing,
                    onRefresh = viewModel::refresh
                ) {
                    val lazyListState = rememberLazyListState()
                    lazyListState.onBottomReached {
                        viewModel.loadMore()
                    }
                    ArticleListContent(
                        modifier = Modifier.matchParentSize(),
                        articleState = viewModel.state,
                        lazyListState = lazyListState,
                        onClickArticle = {
                            navController.navigateToWebViewScreen(it)
                        },
                        onClickBanner = {
                            navController.navigateToWebViewScreen(it)
                        },
                        onCollect = {
                            viewModel.collectOrUnCollectArticle(it)
                        }
                    )
                }
            }
        }
    }
}

val NewColor = Color(0xFF789bc5)
val StickColor = Color(0xFFeab38d)

@Composable
private fun ArticleListContent(
    modifier: Modifier = Modifier,
    articleState: ArticleListViewModel.ArticleState,
    lazyListState: LazyListState = rememberLazyListState(),
    onClickArticle: (article: ArticleList.ArticleUiBean) -> Unit = {},
    onClickBanner: (banner: ArticleList.BannerUiBean) -> Unit = {},
    onCollect: ((bean: ArticleList.ArticleUiBean) -> Unit)? = null,
) {

    val pagerState = rememberInfiniteLoopPagerState()

    LoadingBox(
        modifier,
        loadingContent = {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(state = rememberScrollState(), enabled = false)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(2f / 1f)
                        .background(WanTheme.colors.level4BackgroundColor.copy(0.3f))
                ) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .background(WanTheme.colors.level4BackgroundColor)
                            .height(20.dp)
                            .fillMaxWidth()
                    )
                }
                repeat(20) {
                    Column(
                        modifier = Modifier.background(
                            WanTheme.colors.level1BackgroundColor
                        )
                    ) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(40.dp, 15.dp)
                                    .background(
                                        WanTheme.colors.level4BackgroundColor,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )

                            Box(
                                Modifier
                                    .padding(vertical = 10.dp)
                                    .size(150.dp, 15.dp)
                                    .background(
                                        WanTheme.colors.level4BackgroundColor,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )

                            Box(
                                Modifier
                                    .size(80.dp, 15.dp)
                                    .background(
                                        WanTheme.colors.level4BackgroundColor,
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )

                        }
                        Divider(
                            Modifier.fillMaxWidth(),
                            color = WanTheme.colors.level4BackgroundColor
                        )
                    }

                }
            }
        }
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
                .background(WanTheme.colors.level2BackgroundColor),
            state = lazyListState
        ) {
            if (articleState.isOpenBanner) {
                item(key = "Banner") {
                    val scope = rememberCoroutineScope()
                    val isDragging by pagerState.interactionSource.collectIsDraggedAsState()
                    LaunchedEffect(isDragging) {
                        if (!isDragging) {
                            while (true) {
                                delay(3000L)
                                scope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }.join()
                            }
                        }
                    }

                    Box(
                        Modifier
                            .animateItemPlacement()
                            .fillMaxWidth()
                            .aspectRatio(2f / 1f)
                    ) {

                        var curTitle by remember { mutableStateOf<String?>(null) }

                        LaunchedEffect(Unit) {
                            snapshotFlow { pagerState.currentPageInInfinitePage(articleState.bannerList.size) }.collect {
                                curTitle = articleState.bannerList.getOrNull(it)?.title
                            }
                        }

                        InfiniteLoopHorizontalPager(
                            modifier = Modifier
                                .matchParentSize()
                                .background(WanTheme.colors.level1BackgroundColor),
                            realPageCount = articleState.bannerList.size,
                            state = pagerState
                        ) {
                            val banner = articleState.bannerList.getOrNull(it)
                            if (banner != null) {
                                AsyncImage(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = ripple()
                                        ) {
                                            onClickBanner(
                                                articleState.bannerList[pagerState.currentPageInInfinitePage(
                                                    articleState.bannerList.size
                                                )]
                                            )
                                        },
                                    model = banner.imgUrl,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.ic_loading_pic),
                                    fallback = painterResource(R.drawable.ic_loading_pic),
                                )
                            }
                        }

                        Box(
                            Modifier
                                .align(Alignment.BottomCenter)
                                .background(WanTheme.colors.level4BackgroundColor)
                                .padding(horizontal = 5.dp)
                                .heightIn(min = 20.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = curTitle.orEmpty(),
                                style = WanTheme.typography.h7.copy(
                                    color = WanTheme.colors.level3TextColor
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.fillMaxWidth(0.5f)
                            )
                        }
                    }
                }
            }
            items(articleState.articleList, key = { it.id }) {
                ArticleListSingleBean(
                    modifier = Modifier
                        .animateItemPlacement()
                        .fillMaxWidth()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple()
                        ) {
                            onClickArticle(it)
                        }
                        .padding(10.dp),
                    bean = it,
                    onCollect = onCollect
                )
                Divider(Modifier.fillMaxWidth(), color = WanTheme.colors.level4BackgroundColor)
            }
            if (articleState.loadingMore) {
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

}

@Composable
fun ArticleListSingleBean(
    modifier: Modifier = Modifier,
    bean: ArticleList.ArticleUiBean,
    onCollect: ((bean: ArticleList.ArticleUiBean) -> Unit)? = null,
) {

    ConstraintLayout(modifier) {
        val (
            topStartContent,
            topEndContent,
            titleAndDescContent,
            bottomStartContent,
            bottomEndContent,
        ) = createRefs()

        Row(
            Modifier.constrainAs(topStartContent) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            if (bean.isNew) {
                Text(
                    text = "新",
                    style = WanTheme.typography.h8.copy(
                        color = NewColor,
                    )
                )
            }
            Text(
                text = remember(bean.authorOrSharedUser) {
                    if (bean.authorOrSharedUser.author.isNotEmpty()) {
                        "作者：${bean.authorOrSharedUser.author}"
                    } else {
                        "分享者：${bean.authorOrSharedUser.sharedUser}"
                    }
                },
                style = WanTheme.typography.h8.copy(
                    color = WanTheme.colors.level3TextColor
                )
            )
            bean.tags.forEach {
                Text(
                    text = it.name,
                    modifier = Modifier
                        .border(1.dp, color = NewColor, shape = RoundedCornerShape(5.dp))
                        .padding(horizontal = 2.dp, vertical = 1.dp),
                    style = WanTheme.typography.h8.copy(
                        color = NewColor
                    )
                )
            }
        }
        Text(
            modifier = Modifier.constrainAs(topEndContent) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            text = bean.date,
            style = WanTheme.typography.h8.copy(
                color = WanTheme.colors.level2TextColor
            )
        )
        Row(
            Modifier
                .constrainAs(titleAndDescContent) {
                    top.linkTo(topStartContent.bottom)
                    start.linkTo(parent.start)
                }
                .padding(vertical = 10.dp)
        ) {

            Row {
                if (!bean.envelopePic.isNullOrEmpty()) {
                    AsyncImage(
                        model = bean.envelopePic,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(150.dp, 100.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        placeholder = painterResource(id = R.drawable.ic_loading_pic),
                        error = painterResource(id = R.drawable.ic_loading_pic),
                        fallback = painterResource(id = R.drawable.ic_loading_pic),
                        contentScale = ContentScale.Crop
                    )
                }
                Column {
                    Text(
                        text = bean.title,
                        style = WanTheme.typography.h7.copy(
                            color = WanTheme.colors.level1TextColor
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (bean.desc.isNotEmpty()) {
                        Spacer(Modifier.height(5.dp))
                        Text(
                            text = bean.desc,
                            style = WanTheme.typography.h8.copy(
                                color = WanTheme.colors.level3TextColor
                            ),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }


        }

        Row(Modifier.constrainAs(bottomStartContent) {
            top.linkTo(titleAndDescContent.bottom)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
        }, verticalAlignment = Alignment.CenterVertically) {
            if (bean.isStick) {
                Text(
                    text = "置顶",
                    style = WanTheme.typography.h8.copy(
                        color = StickColor,
                    ),
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
            Text(
                text = remember(bean.chapter) {
                    "分类：${bean.chapter.superChapterName} / ${bean.chapter.chapterName}"
                },
                style = WanTheme.typography.h8.copy(
                    color = WanTheme.colors.level3TextColor
                )
            )
        }
        Crossfade(targetState = bean.isCollect, label = "", modifier = Modifier
            .constrainAs(bottomEndContent) {
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false)
            ) { onCollect?.invoke(bean) }
        ) { isCollect ->
            Image(
                painter = painterResource(id = if (isCollect) R.drawable.ic_like2 else R.drawable.ic_like),
                contentDescription = null,
                modifier = Modifier
                    .padding(2.dp)
                    .size(20.dp),
                colorFilter = if (isCollect) ColorFilter.tint(CollectColor) else null
            )
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewArticleListContent() {
    CompositionLocalProvider(
        LocalLoadingBoxIsLoading provides true
    ) {
        ArticleListContent(Modifier.fillMaxSize(), articleState = remember {
            ArticleListViewModel.ArticleState()
        })
    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewArticleListContent2() {
    WanTheme(colors = DarkColors) {
        CompositionLocalProvider(
            LocalLoadingBoxIsLoading provides false
        ) {
            ArticleListContent(Modifier.fillMaxSize(), articleState = remember {
                ArticleListViewModel.ArticleState(
                    (0L..100L).map {
                        ArticleList.ArticleUiBean(
                            title = "我是标题我是标题我是标题我是标题我是标题我是标题",
                            date = "1小时之前",
                            isNew = true,
                            isStick = true,
                            chapter = ArticleList.ArticleUiBean.Chapter(
                                superChapterName = "广场Tab",
                                chapterName = "自助"
                            ),
                            authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(
                                author = "小茗同学",
                            ),
                            id = it,
                            isCollect = (it % 2) == 0L,
                            desc = "我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述"
                        )
                    }
                )
            })
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewArticleListContent3() {
    WanTheme(colors = LightColors) {
        CompositionLocalProvider(
            LocalLoadingBoxIsLoading provides false
        ) {
            ArticleListContent(Modifier.fillMaxSize(), articleState = remember {
                ArticleListViewModel.ArticleState(
                    (0L..100L).map {
                        ArticleList.ArticleUiBean(
                            envelopePic = if ((it % 2).toInt() == 0) " " else null,
                            title = "我是标题我是标题我是标题我是标题我是标题我是标题",
                            date = "1小时之前",
                            isNew = true,
                            isStick = true,
                            chapter = ArticleList.ArticleUiBean.Chapter(
                                superChapterName = "广场Tab",
                                chapterName = "自助"
                            ),
                            authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(
                                author = "小茗同学",
                            ),
                            id = it,
                            isCollect = (it % 2) == 0L,
                            desc = "我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述我是描述",
                            tags = listOf(
                                ArticleList.ArticleUiBean.Tag(
                                    name = "哈哈哈",
                                    url = "134"
                                )
                            )
                        )
                    }
                )
            })
        }
    }
}






