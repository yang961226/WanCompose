package com.sundayting.wancompose.page.homescreen.article.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val homePageService: HomePageService,
) {

    suspend fun fetchHomePageTopArticle() = homePageService.fetchTopArticleList()

    suspend fun fetchHomePageArticle(page: Int) = homePageService.fetchArticleList(page)
    suspend fun fetchHomePageBanner() = homePageService.fetchBanner()
}