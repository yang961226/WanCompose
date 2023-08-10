package com.sundayting.wancompose.page.homescreen.article.repo

import de.jensklingenberg.ktorfit.http.GET
import kotlinx.serialization.Serializable


@Serializable
abstract class TestBaseNetBean<T> {
    abstract val data: T?
    abstract val errorCode: Int
    abstract val errorMsg: String
}

@Serializable
data class HomePageBannerBean2(
    val imagePath: String,
    val url: String,
    val id: Int,
    val title: String,
)


@Serializable
class HomePageBannerResultBean(
    override val data: List<HomePageBannerBean2>?,
    override val errorCode: Int,
    override val errorMsg: String,
) : TestBaseNetBean<List<HomePageBannerBean2>>()

interface HomePageService2 {

    @GET("banner/json/")
    suspend fun getHomePage(): HomePageBannerResultBean

}