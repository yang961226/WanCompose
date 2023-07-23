package com.sundayting.wancompose.homescreen.article.repo

interface HomePageService {

    suspend fun fetchHomePageArticle(page: Int): HomePageArticleBean

    suspend fun fetchHomePageBanner(): HomePageBannerBean

}