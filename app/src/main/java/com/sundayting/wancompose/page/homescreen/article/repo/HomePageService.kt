package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.network.NetResult
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path


interface HomePageService {

    /**
     * 抓取文章列表
     */
    @GET("article/list/{page}/json")
    suspend fun fetchHomePageArticle(@Path("page") page: Int): NetResult<HomePageArticleBean>

    /**
     * 抓取置顶列表
     */
    @GET("article/top/json")
    suspend fun fetchHomePageTopArticle(): NetResult<HomePageTopArticleBean>

    @GET("banner/json")
    suspend fun fetchHomePageBanner(): NetResult<HomePageBannerBean>

}