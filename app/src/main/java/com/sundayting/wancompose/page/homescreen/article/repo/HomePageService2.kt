package com.sundayting.wancompose.page.homescreen.article.repo

import com.sundayting.wancompose.network.WanNResult
import com.sundayting.wancompose.network.okhttp.NResult
import retrofit2.http.GET
import retrofit2.http.Path

interface HomePageService2 {


    /**
     * 抓取文章列表
     */
    @GET("article/list/{page}/json")
    suspend fun fetchHomePageArticle(@Path("page") page: Int): NResult<WanNResult<ArticleResultBean>>

}
