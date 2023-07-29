package com.sundayting.wancompose.page.homescreen.article.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val homePageService: HomePageService,
) {


    suspend fun fetchHomePageArticle2(page: Int) = homePageService.fetchHomePageArticle(page)

    suspend fun fetchHomePageBanner2() = homePageService.fetchHomePageBanner()
}