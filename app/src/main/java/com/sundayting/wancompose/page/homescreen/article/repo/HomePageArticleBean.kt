package com.sundayting.wancompose.page.homescreen.article.repo

import com.squareup.moshi.Json
import com.sundayting.wancompose.network.WanNetResult
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import kotlinx.serialization.Serializable

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

data class ArticleResultBean(
    @field:Json(name = "curPage")
    val curPage: Int,
    @field:Json(name = "datas")
    val list: List<ArticleBean>,
)

@Serializable
data class HomePageBannerResultBean(
    val imagePath: String,
    val url: String,
    val id: Int,
    val title: String,
)

@Serializable
data class HomePageBannerBean(
    override val data: List<HomePageBannerResultBean>?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNetResult<List<HomePageBannerResultBean>>()

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

fun HomePageBannerResultBean.toBannerUiBean(): ArticleList.BannerUiBean {
    return ArticleList.BannerUiBean(
        imgUrl = imagePath,
        linkUrl = url,
        id = id,
        title = title,
    )
}