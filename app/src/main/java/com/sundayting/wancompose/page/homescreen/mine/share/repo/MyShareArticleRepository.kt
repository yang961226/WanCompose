package com.sundayting.wancompose.page.homescreen.mine.share.repo

import com.sundayting.wancompose.page.homescreen.article.repo.ArticleService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyShareArticleRepository @Inject constructor(
    private val service: ArticleService,
) {

    suspend fun fetchCollectedArticle(page: Int) = service.fetchCollectedArticle(page)

}