package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.network.okhttp.NResult
import retrofit2.http.GET
import retrofit2.http.Path

interface HomePageService {


    /**
     * 抓取置顶列表
     */
    @GET("article/top/json")
    suspend fun fetchHomePageTopArticle(): NResult<WanNResult<List<ArticleBean>>>

    /**
     * 抓取文章列表
     */
    @GET("article/list/{page}/json")
    suspend fun fetchHomePageArticle(@Path("page") page: Int): NResult<WanNResult<ArticleListBean>>

    @GET("banner/json")
    suspend fun fetchHomePageBanner(): NResult<WanNResult<List<HomePageBannerBean>>>

}
