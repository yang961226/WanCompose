package com.sundayting.wancompose.page.myshare

import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.event.ArticleSharedChangeEvent
import com.sundayting.wancompose.common.event.ArticleSharedDeleteEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleService
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyShareArticleRepository @Inject constructor(
    private val service: ArticleService,
    private val eventManager: EventManager,
) {

    private val scope = CoroutineScope(SupervisorJob())

    var cachedArticleListSuccess = false
    val cachedArticleList = mutableListOf<ArticleList.ArticleUiBean>()

    suspend fun shareArticle(
        title: String,
        link: String,
    ) = service.shareArticle(title, link)

    suspend fun deleteSharedArticle(id: Long) = service.deleteSharedArticle(id)

    suspend fun fetchSharedArticle(page: Int) = service.fetchSharedArticle(page)

    init {
        scope.launch {
            launch {
                eventManager.eventFlow.filterIsInstance<ArticleSharedChangeEvent>()
                    .collect { event ->
                        if (!cachedArticleListSuccess) {
                            return@collect
                        }
                        cachedArticleList.add(event.bean)
                    }
            }

            launch {
                eventManager.eventFlow.filterIsInstance<ArticleSharedDeleteEvent>()
                    .collect { event ->
                        cachedArticleList.removeIf { it.id == event.bean.id }
                        eventManager.emitToast(R.string.delete_success)
                    }
            }
        }
    }

}