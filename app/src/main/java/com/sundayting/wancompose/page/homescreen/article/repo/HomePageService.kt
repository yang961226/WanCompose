package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import de.jensklingenberg.ktorfit.http.GET
import kotlinx.serialization.Serializable


interface HomePageService {

    @GET("banner/json/")
    suspend fun getBanner(): NResult<HomePageBannerResultBean>

}


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

