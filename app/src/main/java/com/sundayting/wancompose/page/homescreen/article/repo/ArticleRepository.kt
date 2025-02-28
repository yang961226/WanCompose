package com.sundayting.wancompose.page.homescreen.article.repo

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sundayting.wancompose.db.WanDatabase
import com.sundayting.wancompose.page.setting.SettingViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleRepository @Inject constructor(
    private val articleService: ArticleService,
    private val wanDatabase: WanDatabase,
    dataStore: DataStore<Preferences>,
) {

    suspend fun fetchHomePageTopArticle() = articleService.fetchTopArticleList()

    suspend fun fetchHomePageArticle(page: Int) = articleService.fetchArticleList(page)
    suspend fun fetchHomePageBanner() = articleService.fetchBanner()

    suspend fun collectArticle(id: Long) = articleService.collectArticle(id)
    suspend fun unCollectArticle(id: Long) = articleService.unCollectArticle(id)

    suspend fun searchArticle(page: Int, keyWord: String) =
        articleService.searchArticle(page, keyWord)

    val openBannerFlow = dataStore.data.map { it[SettingViewModel.openBannerKey] ?: true }

//    suspend fun insertArticles(articleList: List<ArticleBean>) =
//        wanDatabase.articleDao().insertArticles(articleList)
//
//    suspend fun insertArticles(articleList: List<ArticleBean>, clearFirst: Boolean = false) {
//        if (articleList.isEmpty()) {
//            return
//        }
//        wanDatabase.withTransaction {
//            if (clearFirst) {
//                wanDatabase.articleDao().deleteUsersArticle(articleList.first().ownerId)
//            }
//            wanDatabase.articleDao().insertArticles(articleList)
//        }
//    }

}