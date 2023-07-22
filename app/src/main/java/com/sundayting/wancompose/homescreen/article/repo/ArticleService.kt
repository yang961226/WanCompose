package com.sundayting.wancompose.homescreen.article.repo

interface ArticleService {

    suspend fun fetchHomePageArticle(page: Int): HomePageArticleBean

}