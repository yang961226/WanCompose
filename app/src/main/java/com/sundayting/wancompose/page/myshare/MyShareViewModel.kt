package com.sundayting.wancompose.page.myshare

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.network.NetExceptionHandler
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.toArticleUiBean
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyShareViewModel @Inject constructor(
    private val repo: MyShareArticleRepository,
    private val articleRepo: ArticleRepository,
    private val eventManager: EventManager,
) : ViewModel() {

    val state = MyShareArticleUiState(repo.cachedArticleList)

    private var page: Int = 1

    private var loadJob: Job? = null

    init {
        if (!repo.cachedArticleListSuccess) {
            loadMore()
        }
        viewModelScope.launch {
            eventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>().collect { event ->
                state.changeArticleCollectState(event.bean.id, event.tryCollect)
            }
        }
    }

    private var collectJob: Job? = null

    fun collectOrUnCollect(bean: ArticleList.ArticleUiBean, tryCollect: Boolean) {
        if (collectJob?.isActive == true) {
            return
        }
        collectJob = viewModelScope.launch {
            if ((if (tryCollect) {
                    articleRepo.collectArticle(bean.id)
                } else {
                    articleRepo.unCollectArticle(bean.id)
                }).isSuccess()
            ) {
                eventManager.emitCollectArticleEvent(bean, tryCollect)
            }
        }
    }

    fun loadMore() {
        if (loadJob?.isActive == true || !state.canLoadMore || repo.cachedArticleListSuccess) {
            return
        }
        loadJob = viewModelScope.launch(NetExceptionHandler) {
            state.isLoadingMore = true
            val result = repo.fetchSharedArticle(page)
            if (result.isSuccess()) {
                repo.cachedArticleListSuccess = true
                val data = result.body.requireData()
                state.canLoadMore = data.shareArticles.curPage < data.shareArticles.pageCount
                val list = data.shareArticles.datas.map { it.toArticleUiBean() }
                state.addArticleList(list)
                repo.cachedArticleList.addAll(list)
            }
        }.also {
            it.invokeOnCompletion {
                state.isLoadingMore = false
            }
        }
    }

    @Stable
    class MyShareArticleUiState(list: List<ArticleList.ArticleUiBean> = listOf()) {

        var isLoadingMore by mutableStateOf(false)
        var canLoadMore by mutableStateOf(true)

        private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>().apply {
            addAll(list)
        }
        val articleList: List<ArticleList.ArticleUiBean> = _articleList

        fun addArticleList(list: List<ArticleList.ArticleUiBean>) {
            _articleList.addAll(list)
        }

        fun changeArticleCollectState(id: Long, isCollect: Boolean) {
            val index = _articleList.indexOfFirst { it.id == id }
            if (index != -1) {
                _articleList[index] = _articleList[index].copy(isCollect = isCollect)
            }
        }

    }


}