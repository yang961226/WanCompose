package com.sundayting.wancompose.homescreen.article.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val homePageService: HomePageService,
) {

    suspend fun fetchHomePageArticle(page: Int) = homePageService.fetchHomePageArticle(page)

    suspend fun fetchHomePageBanner() = homePageService.fetchHomePageBanner()

}