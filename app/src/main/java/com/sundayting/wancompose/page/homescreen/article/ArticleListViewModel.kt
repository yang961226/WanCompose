package com.sundayting.wancompose.page.homescreen.article

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.function.UserLoginFunction.VISITOR_ID
import com.sundayting.wancompose.network.NetExceptionHandler
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

    val state = ArticleState()

    init {
        viewModelScope.launch {
            eventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>().collect { event ->
                val article = state.articleList.firstOrNull { it.id == event.id } ?: return@collect
                article.isCollect = event.isCollect
            }
        }
    }

    @Stable
    class ArticleState(
        list: List<ArticleList.ArticleUiBean> = listOf(),
    ) {

        private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>().apply {
            addAll(list)
        }
        val articleList: List<ArticleList.ArticleUiBean> = _articleList

        fun addArticle(list: List<ArticleList.ArticleUiBean>, clearFirst: Boolean = false) {
            if (clearFirst) {
                _articleList.clear()
            }
            _articleList.addAll(list)
        }

        val bannerList = mutableStateListOf<ArticleList.BannerUiBean>()

        var refreshing by mutableStateOf(false)
        var loadingMore by mutableStateOf(false)

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

    fun collectOrUnCollectArticle(id: Long, isCollect: Boolean) {
        if (mineRepo.curUserFlow.value == null) {
            eventManager.emitEvent(ShowLoginPageEvent)
            return
        }
        viewModelScope.launch {
            if (isCollect) {
                if (repo.collectArticle(id).isSuccess()) {
                    eventManager.emitCollectArticleEvent(id, true)
                }
            } else {
                if (repo.unCollectArticle(id).isSuccess()) {
                    eventManager.emitCollectArticleEvent(id, false)
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
        loadJob = viewModelScope.launch(NetExceptionHandler) {
            joinAll(
                launch {
                    if (isRefresh) {
                        val result = repo.fetchHomePageBanner()
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