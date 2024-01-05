package com.sundayting.wancompose.page.homescreen.mine.share.repo

import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleService
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyCollectedArticleRepository @Inject constructor(
    private val service: ArticleService,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    var cachedArticleListSuccess = false
    val cachedArticleList = mutableListOf<ArticleList.ArticleUiBean>()

    init {
        scope.launch {
            EventManager.getInstance().eventFlow.filterIsInstance<ArticleCollectChangeEvent>()
                .collect { event ->
                    if (event.isCollect) {
                        cachedArticleList.add(event.bean)
                    } else {
                        cachedArticleList.removeIf {
                            it.id == event.bean.id
                        }
                    }
                }
        }
    }

    suspend fun fetchCollectedArticle(page: Int) = service.fetchCollectedArticle(page)

}