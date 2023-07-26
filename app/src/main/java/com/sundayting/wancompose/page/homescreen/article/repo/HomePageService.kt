package com.sundayting.wancompose.page.homescreen.article.repo

interface HomePageService {

    suspend fun fetchHomePageArticle(page: Int): HomePageArticleBean

    suspend fun fetchHomePageBanner(): HomePageBannerBean

}