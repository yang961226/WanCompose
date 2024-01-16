package com.sundayting.wancompose.page.homescreen.article.repo

import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_COMPACT
import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.page.homescreen.article.ArticleBean
import com.sundayting.wancompose.page.homescreen.article.ArticleResultBean
import com.sundayting.wancompose.page.homescreen.article.CollectResult
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


interface ArticleService {

    @GET("banner/json/")
    suspend fun fetchBanner(): NResult<HomePageBannerResultBean>

    @GET("article/list/{path}/json/")
    suspend fun fetchArticleList(@Path("path") page: Int): NResult<ArticleResultBean>

    @POST("lg/collect/{id}/json/")
    suspend fun collectArticle(@Path("id") id: Long): NResult<CollectResult>

    @POST("lg/uncollect_originId/{id}/json")
    suspend fun unCollectArticle(@Path("id") id: Long): NResult<CollectResult>

    @GET("article/top/json/")
    suspend fun fetchTopArticleList(): NResult<TopArticleResultBean>

    @GET("lg/collect/list/{page}/json")
    suspend fun fetchCollectedArticle(@Path("page") page: Int): NResult<MyCollectArticleResultBean>

}

@Serializable
class MyCollectArticleBean(
    val author: String,
    val chapterId: Int,
    val chapterName: String,
    val courseId: Int,
    val desc: String,
    val envelopePic: String,
    val id: Long,
    val link: String,
    val niceDate: String,
    val origin: String,
    val originId: Long,
    val publishTime: Long,
    val title: String,
    val userId: Long,
)

@Serializable
class MyCollectArticleDataBean(
    val curPage: Int,
    val pageCount: Int,
    @SerialName("datas")
    val list: List<MyCollectArticleBean>,
)

@Serializable
class MyCollectArticleResultBean(
    override val data: MyCollectArticleDataBean?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<MyCollectArticleDataBean>()

fun MyCollectArticleBean.toArticleUiBean(): ArticleList.ArticleUiBean {
    return ArticleList.ArticleUiBean(
        title = HtmlCompat.fromHtml(this.title, FROM_HTML_MODE_COMPACT).toString(),
        date = niceDate,
        id = originId,
        chapter = ArticleList.ArticleUiBean.Chapter(
            chapterName = chapterName,
            superChapterName = ""
        ),
        authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(author = author),
        link = link,
        isCollect = true
    )
}


@Serializable
class TopArticleResultBean(
    override val data: List<ArticleBean>?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<List<ArticleBean>>()


@Serializable
data class HomePageBannerBean(
    val imagePath: String,
    val url: String,
    val id: Long,
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
    override val data: List<HomePageBannerBean>?,
    override val errorCode: Int,
    override val errorMsg: String,
) : WanNResult<List<HomePageBannerBean>>()

