package com.sundayting.wancompose.page.homescreen.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.function.UserLoginFunction.VISITOR_ID
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.repo.toBannerUiBean
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
class ArticleListViewModel @Inject constructor(
    private val repo: ArticleRepository,
    private val mineRepo: MineRepository,
    private val eventManager: EventManager,
) : ViewModel() {

    val state = ArticlePageState()

    init {
        viewModelScope.launch {
            eventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>().collect { event ->
                val index =
                    state.articleList.indexOfFirst { it.id == event.bean.id }.takeIf { it != -1 }
                        ?: return@collect

                state.changeArticle(
                    index,
                    state.articleList[index].copy(isCollect = event.tryCollect)
                )
            }
        }
        viewModelScope.launch {
            repo.openBannerFlow.collect {
                state.isOpenBanner = it
            }
        }
    }

    private var curPage = 0

    init {
        viewModelScope.launch {
            launch {
                mineRepo.curUidFlow.collect {
                    refresh()
                }
            }
        }
    }

    private var changeCollectJob: Job? = null

    fun collectOrUnCollectArticle(bean: ArticleList.ArticleUiBean) {
        if (mineRepo.curUserFlow.value == null && changeCollectJob?.isActive == true) {
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

    fun refresh() {
        load(true)
    }

    fun loadMore() {
        load(false)
    }

    private var loadJob: Job? = null

    private fun load(isRefresh: Boolean) {
        if (loadJob?.isActive == true) {
            return
        }
        if (isRefresh) {
            curPage = 0
            state.refreshing = true
        } else {
            state.loadingMore = true
        }
        loadJob = viewModelScope.launch {
            joinAll(
                launch {
                    if (isRefresh) {
                        val result = repo.fetchHomePageBanner()
                        state.isShowLoadingBox = false
                        if (result.isSuccess()) {
                            result.body.requireData().let { list ->
                                state.bannerList.clear()
                                state.bannerList.addAll(list.map { it.toBannerUiBean() })
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
                        val result = repo.fetchHomePageArticle(curPage)
                        if (result.isSuccess()) {
                            result.body.requireData().list
                        } else {
                            null
                        }
                    }
                    curPage++
                    val curUserId = mineRepo.curUserFlow.firstOrNull()?.id ?: VISITOR_ID
                    val topArticleList = topArticleListDeferred.await().orEmpty()
                        .map { it.copy(ownerId = curUserId, isStick = true) }
                    val articleList = articleListDeferred.await().orEmpty().map {
                        it.copy(ownerId = curUserId)
                    }
                    state.addArticle(
                        (topArticleList + articleList).map { it.toArticleUiBean() },
                        isRefresh
                    )
                },
            )
        }.apply {
            invokeOnCompletion {
                state.refreshing = false
                state.loadingMore = false
            }
        }
    }

}