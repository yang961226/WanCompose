package com.sundayting.wancompose.page.homescreen.article.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
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
import com.sundayting.wancompose.theme.WanColors
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable

object ArticleList : HomeScreen.HomeScreenPage {

    override val route: String
        get() = "文章列表"

    @Stable
    @Serializable
    data class ArticleUiBean(
        val title: String,
        val date: String,
        val id: Long,
        val isStick: Boolean = false,
        val isNew: Boolean = false,
        val chapter: Chapter,
        val authorOrSharedUser: AuthorOrSharedUser,
        val link: String = "",
        val isCollect: Boolean = false,
    ) {

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
        onClickArticle: (article: ArticleUiBean) -> Unit = {},
        onClickBanner: (banner: BannerUiBean) -> Unit = {},
    ) {

        CompositionLocalProvider(
            LocalLoadingBoxIsLoading provides viewModel.state.isShowLoadingBox
        ) {
            val pullRefreshState =
                rememberPullRefreshState(viewModel.state.refreshing, viewModel::refresh)

            TitleBarWithContent(
                modifier,
                titleBarContent = {
                    Text(
                        stringResource(id = R.string.bottom_tab_home), style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ), modifier = Modifier.align(Alignment.Center)
                    )
                }
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .pullRefresh(pullRefreshState)
                ) {
                    val lazyListState = rememberLazyListState()
                    lazyListState.onBottomReached {
                        viewModel.loadMore()
                    }
                    ArticleListContent(
                        modifier = Modifier.matchParentSize(),
                        articleState = viewModel.state,
                        lazyListState = lazyListState,
                        onClickArticle = onClickArticle,
                        onClickBanner = onClickBanner,
                        onCollect = {
                            viewModel.collectOrUnCollectArticle(it)
                        }
                    )
                    PullRefreshIndicator(
                        viewModel.state.refreshing,
                        pullRefreshState,
                        Modifier.align(Alignment.TopCenter)
                    )
                }
            }
        }
    }
}

private val newColor = Color(0xFF789bc5)
private val stickColor = Color(0xFFeab38d)

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
                        .background(Color.LightGray.copy(0.5f))
                ) {
                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color.LightGray.copy(0.7f))
                            .height(20.dp)
                            .fillMaxWidth()
                    )
                }
                repeat(20) {
                    Column {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(40.dp, 15.dp)
                                    .background(
                                        Color.LightGray.copy(0.5f),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )

                            Box(
                                Modifier
                                    .padding(vertical = 10.dp)
                                    .size(150.dp, 15.dp)
                                    .background(
                                        Color.LightGray.copy(0.5f),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )

                            Box(
                                Modifier
                                    .size(80.dp, 15.dp)
                                    .background(
                                        Color.LightGray.copy(0.5f),
                                        shape = RoundedCornerShape(5.dp)
                                    )
                            )

                        }
                        Divider(Modifier.fillMaxWidth())
                    }

                }
            }
        }
    ) {
        LazyColumn(Modifier.fillMaxSize(), state = lazyListState) {
            item(key = "Banner") {
                val isDragging by pagerState.interactionSource.collectIsDraggedAsState()
                LaunchedEffect(isDragging) {
                    if (!isDragging) {
                        while (true) {
                            delay(3000L)
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
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
                        pagerState.scrollToPage(Int.MAX_VALUE / 2)
                        snapshotFlow { pagerState.currentPageInInfinitePage(articleState.bannerList.size) }.collect {
                            curTitle = articleState.bannerList.getOrNull(it)?.title
                        }
                    }

                    InfiniteLoopHorizontalPager(
                        modifier = Modifier.matchParentSize(),
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
                                        indication = rememberRipple()
                                    ) {
                                        onClickBanner(
                                            articleState.bannerList[pagerState.currentPageInInfinitePage(
                                                articleState.bannerList.size
                                            )]
                                        )
                                    },
                                model = ImageRequest
                                    .Builder(LocalContext.current)
                                    .diskCachePolicy(CachePolicy.ENABLED)
                                    .crossfade(true)
                                    .data(banner.imgUrl)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop
                            )
                        }

                    }

                    Box(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(0.2f))
                            .padding(horizontal = 5.dp)
                            .heightIn(min = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = curTitle.orEmpty(),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth(0.5f)
                        )
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
                            indication = rememberRipple()
                        ) {
                            onClickArticle(it)
                        }
                        .padding(10.dp),
                    bean = it,
                    onCollect = onCollect
                )
                Divider(Modifier.fillMaxWidth())
            }
            if (articleState.loadingMore) {
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
            titleContent,
            bottomStartContent,
            bottomEndContent,
        ) = createRefs()

        Row(
            Modifier.constrainAs(topStartContent) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
            },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (bean.isNew) {
                Text(
                    text = "新",
                    style = TextStyle(
                        color = newColor,
                    ),
                    modifier = Modifier.padding(end = 5.dp)
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
                style = TextStyle(
                    color = Color.Black.copy(0.6f)
                )
            )
        }
        Text(
            modifier = Modifier.constrainAs(topEndContent) {
                top.linkTo(parent.top)
                end.linkTo(parent.end)
            },
            text = bean.date,
            style = TextStyle(
                color = Color.Black.copy(0.6f)
            )
        )
        Text(
            modifier = Modifier
                .constrainAs(titleContent) {
                    top.linkTo(topStartContent.bottom)
                    start.linkTo(parent.start)
                }
                .padding(vertical = 10.dp),
            text = bean.title
        )
        Row(Modifier.constrainAs(bottomStartContent) {
            top.linkTo(titleContent.bottom)
            start.linkTo(parent.start)
            bottom.linkTo(parent.bottom)
        }, verticalAlignment = Alignment.CenterVertically) {
            if (bean.isStick) {
                Text(
                    text = "置顶",
                    style = TextStyle(
                        color = stickColor
                    ),
                    modifier = Modifier.padding(end = 5.dp)
                )
            }
            Text(
                text = remember(bean.chapter) {
                    "分类：${bean.chapter.superChapterName} / ${bean.chapter.chapterName}"
                },
                style = TextStyle(
                    color = Color.Black.copy(0.6f)
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
                indication = rememberRipple(bounded = false)
            ) { onCollect?.invoke(bean) }
        ) { isCollect ->
            Image(
                painter = painterResource(id = if (isCollect) R.drawable.ic_like2 else R.drawable.ic_like),
                contentDescription = null,
                modifier = Modifier
                    .padding(2.dp)
                    .size(20.dp),
                colorFilter = if (isCollect) ColorFilter.tint(WanColors.CollectColor) else null
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
                        isCollect = (it % 2) == 0L
                    )
                }
            )
        })
    }

}






