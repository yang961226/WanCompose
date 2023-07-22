package com.sundayting.wancompose.homescreen.repo

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val articleService: ArticleService,
) {

    suspend fun fetchHomePageArticle(page: Int) = articleService.fetchHomePageArticle(page)

}