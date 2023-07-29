package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.network.NetResult
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path


interface HomePageService {

    @GET("article/list/{page}/json")
    suspend fun fetchHomePageArticle(@Path("page") page: Int): NetResult<HomePageArticleBean>

    @GET("banner/json")
    suspend fun fetchHomePageBanner(): NetResult<HomePageBannerBean>

}