package com.sundayting.wancompose.homescreen.article.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sundayting.wancompose.R
import com.sundayting.wancompose.homescreen.HomeScreenViewModel.ArticleListState

object ArticleList {

    @Stable
    class ArticleUiBean(
        val title: String,
        val date: String,
        val id: Long,
        val isStick: Boolean = false,
        val isNew: Boolean = false,
        val chapter: Chapter,
        val authorOrSharedUser: AuthorOrSharedUser,
    ) {

        data class Chapter(
            val chapterName: String,
            val superChapterName: String,
        )

        data class AuthorOrSharedUser(
            val author: String = "",
            val sharedUser: String = "",
        )

        var isLike by mutableStateOf(false)

    }

    @Composable
    fun ArticleScreen(
        modifier: Modifier = Modifier,
        articleListState: ArticleListState,
    ) {

        val pullRefreshState =
            rememberPullRefreshState(articleListState.refreshing, articleListState::refresh)

        Box(modifier.pullRefresh(pullRefreshState)) {
            ArticleListContent(
                modifier = Modifier.matchParentSize(),
                list = articleListState.articleList
            )
            PullRefreshIndicator(
                articleListState.refreshing,
                pullRefreshState,
                Modifier.align(Alignment.TopCenter)
            )
        }

    }
}

private val newColor = Color(0xFF789bc5)
private val stickColor = Color(0xFFeab38d)

@Composable
private fun ArticleListContent(
    modifier: Modifier = Modifier,
    list: List<ArticleList.ArticleUiBean>,
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(modifier, state = state) {
        items(list, key = { it.id }) {
            ArticleListSingleBean(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                bean = it
            )
            Divider(Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun ArticleListSingleBean(
    modifier: Modifier = Modifier,
    bean: ArticleList.ArticleUiBean,
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
        Image(
            painter = painterResource(id = if (bean.isLike) R.drawable.ic_like2 else R.drawable.ic_like),
            contentDescription = null,
            modifier = Modifier
                .padding(2.dp)
                .size(20.dp)
                .constrainAs(bottomEndContent) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            colorFilter = if (bean.isLike) ColorFilter.tint(Color(0xFFe87045)) else null
        )

    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewArticleListContent() {
    ArticleListContent(Modifier.fillMaxSize(), list = remember {
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
                id = it
            )
        }
    })
}






