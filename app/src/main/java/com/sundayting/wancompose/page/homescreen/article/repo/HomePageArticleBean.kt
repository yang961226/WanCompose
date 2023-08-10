package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList

data class ArticleBean(
    val id: Long,
    val title: String,
    val niceDate: String,
    val fresh: Boolean,
    val shareUser: String,
    val author: String,
    val chapterName: String,
    val superChapterName: String,
    val link: String,
)

data class ArticleListBean(
    val curPage: Int,
    val list: List<ArticleBean>,
)

fun ArticleBean.toArticleUiBean(
    isStick: Boolean = false,
): ArticleList.ArticleUiBean {
    return ArticleList.ArticleUiBean(
        title = title,
        date = niceDate,
        isStick = isStick,
        isNew = fresh,
        chapter = ArticleList.ArticleUiBean.Chapter(
            chapterName = chapterName,
            superChapterName = superChapterName,
        ),
        authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(
            author = author,
            sharedUser = shareUser
        ),
        id = id,
        link = link
    )
}