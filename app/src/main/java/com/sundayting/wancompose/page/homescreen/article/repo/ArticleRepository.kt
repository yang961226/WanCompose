package com.sundayting.wancompose.page.homescreen.article.repo

import androidx.room.withTransaction
import com.sundayting.wancompose.db.WanDatabase
import com.sundayting.wancompose.page.homescreen.article.ArticleBean
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val homePageService: HomePageService,
    private val wanDatabase: WanDatabase,
) {

    suspend fun fetchHomePageTopArticle() = homePageService.fetchTopArticleList()

    suspend fun fetchHomePageArticle(page: Int) = homePageService.fetchArticleList(page)
    suspend fun fetchHomePageBanner() = homePageService.fetchBanner()

    fun userArticleFlow(uid: Long) = wanDatabase.articleDao().queryUsersArticleFlow(uid)

    suspend fun insertArticles(articleList: List<ArticleBean>) =
        wanDatabase.articleDao().insertArticles(articleList)

    suspend fun insertArticles(articleList: List<ArticleBean>, clearFirst: Boolean = false) {
        if (articleList.isEmpty()) {
            return
        }
        wanDatabase.withTransaction {
            if (clearFirst) {
                wanDatabase.articleDao().deleteUsersArticle(articleList.first().ownerId)
            }
            wanDatabase.articleDao().insertArticles(articleList)
        }
    }

}