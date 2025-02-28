package com.sundayting.wancompose.page.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.function.UserLoginFunction.VISITOR_ID
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import com.sundayting.wancompose.page.homescreen.article.ArticlePageState
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.repo.toBannerUiBean
import com.sundayting.wancompose.page.homescreen.article.toArticleUiBean
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: ArticleRepository,
    private val mineRepo: MineRepository,
    private val eventManager: EventManager,
) : ViewModel() {

    val articlePageState = ArticlePageState()

    init {
        viewModelScope.launch {
            eventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>().collect { event ->
                val index =
                    articlePageState.articleList.indexOfFirst { it.id == event.bean.id }
                        .takeIf { it != -1 }
                        ?: return@collect

                articlePageState.changeArticle(
                    index,
                    articlePageState.articleList[index].copy(isCollect = event.tryCollect)
                )
            }
        }
        viewModelScope.launch {
            repo.openBannerFlow.collect {
                articlePageState.isOpenBanner = it
            }
        }
        viewModelScope.launch {
            launch {
                mineRepo.curUidFlow.collect {
                    refreshArticle()
                }
            }
        }
    }

    private var changeCollectJob: Job? = null

    fun collectOrUnCollectArticle(bean: ArticleList.ArticleUiBean) {
        if (changeCollectJob?.isActive == true) {
            return
        }
        if (mineRepo.curUserFlow.value == null) {
            eventManager.emitEvent(ShowLoginPageEvent)
            return
        }
        changeCollectJob = viewModelScope.launch {
            if (!bean.isCollect) {
                if (repo.collectArticle(bean.id).isSuccess()) {
                    eventManager.emitCollectArticleEvent(bean, true)
                }
            } else {
                if (repo.unCollectArticle(bean.id).isSuccess()) {
                    eventManager.emitCollectArticleEvent(bean, false)
                }
            }
        }

    }

    fun refreshArticle() = loadArticle(true)
    fun loadMoreArticle() = loadArticle(false)

    private var loadArticle: Job? = null

    private fun loadArticle(isRefresh: Boolean) {
        if (loadArticle?.isActive == true) {
            return
        }
        if (isRefresh) {
            articlePageState.page = 0
            articlePageState.refreshing = true
        } else {
            articlePageState.loadingMore = true
        }
        loadArticle = viewModelScope.launch {
            joinAll(
                launch {
                    if (isRefresh) {
                        val result = repo.fetchHomePageBanner()
                        articlePageState.isShowLoadingBox = false
                        if (result.isSuccess()) {
                            result.body.requireData().let { list ->
                                articlePageState.bannerList.clear()
                                articlePageState.bannerList.addAll(list.map { it.toBannerUiBean() })
                            }
                        }
                    }
                },
                launch {
                    val topArticleListDeferred = async {
                        if (isRefresh) {
                            val result = repo.fetchHomePageTopArticle()
                            if (result.isSuccess()) {
                                result.body.data
                            } else {
                                null
                            }
                        } else {
                            null
                        }
                    }
                    val articleListDeferred = async {
                        val result = repo.fetchHomePageArticle(articlePageState.page)
                        if (result.isSuccess()) {
                            result.body.requireData().list
                        } else {
                            null
                        }
                    }
                    articlePageState.page++
                    val curUserId = mineRepo.curUserFlow.firstOrNull()?.id ?: VISITOR_ID
                    val topArticleList = topArticleListDeferred.await().orEmpty()
                        .map { it.copy(ownerId = curUserId, isStick = true) }
                    val articleList = articleListDeferred.await().orEmpty().map {
                        it.copy(ownerId = curUserId)
                    }
                    articlePageState.addArticle(
                        (topArticleList + articleList).map { it.toArticleUiBean() },
                        isRefresh
                    )
                },
            )
        }.apply {
            invokeOnCompletion {
                articlePageState.refreshing = false
                articlePageState.loadingMore = false
            }
        }
    }

}