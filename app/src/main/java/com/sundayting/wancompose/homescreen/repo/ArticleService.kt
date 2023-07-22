package com.sundayting.wancompose.homescreen.repo

interface ArticleService {

    suspend fun fetchHomePageArticle(page: Int): HomePageArticleBean

}