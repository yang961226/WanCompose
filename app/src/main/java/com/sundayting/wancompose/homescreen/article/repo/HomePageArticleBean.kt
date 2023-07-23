package com.sundayting.wancompose.homescreen.article.repo

import com.sundayting.wancompose.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.network.WanNetResult
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ArticleBean(
    val id: Long,
    val title: String,
    val niceDate: String,
    val fresh: Boolean,
    val shareUser: String,
    val author: String,
    val chapterName: String,
    val superChapterName: String,
)

@Serializable
data class ArticleResultBean(
    val curPage: Int,
    @SerialName("datas")
    val list: List<ArticleBean>,
)

@Serializable
data class HomePageArticleBean(
    override val data: ArticleResultBean?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNetResult<ArticleResultBean?>()

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
        id = id
    )
}