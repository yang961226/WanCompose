package com.sundayting.wancompose.page.homescreen.mine.share.repo

import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleService
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MyCollectedArticleRepository @Inject constructor(
    private val service: ArticleService,
) {

    private val scope = CoroutineScope(SupervisorJob())

    var cachedArticleListSuccess = false
    val cachedArticleList = mutableListOf<ArticleList.ArticleUiBean>()

    init {
        scope.launch {
            EventManager.getInstance().eventFlow.filterIsInstance<ArticleCollectChangeEvent>()
                .collect { event ->
                    if (!cachedArticleListSuccess) {
                        return@collect
                    }
                    if (event.tryCollect) {
                        cachedArticleList.add(
                            event.bean.copy(isCollect = true)
                        )
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