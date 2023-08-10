package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


interface HomePageService {

    @GET("banner/json/")
    suspend fun fetchBanner(): NResult<HomePageBannerResultBean>

    @GET("article/list/{path}/json/")
    suspend fun fetchArticleList(@Path("path") page: Int): NResult<ArticleResultBean>

    @GET("article/top/json/")
    suspend fun fetchTopArticleList(): NResult<TopArticleResultBean>

}

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
    val link: String,
)

@Serializable
data class ArticleListBean(
    val curPage: Int,
    @SerialName("datas")
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

@Serializable
class ArticleResultBean(
    override val data: ArticleListBean,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<ArticleListBean>()


@Serializable
class TopArticleResultBean(
    override val data: List<ArticleBean>,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<List<ArticleBean>>()


@Serializable
data class HomePageBannerBean(
    val imagePath: String,
    val url: String,
    val id: Int,
    val title: String,
)

fun HomePageBannerBean.toBannerUiBean(): ArticleList.BannerUiBean {
    return ArticleList.BannerUiBean(
        imgUrl = this.imagePath,
        linkUrl = this.url,
        id = this.id,
        title = this.title
    )
}


@Serializable
class HomePageBannerResultBean(
    override val data: List<HomePageBannerBean>,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<List<HomePageBannerBean>>()

