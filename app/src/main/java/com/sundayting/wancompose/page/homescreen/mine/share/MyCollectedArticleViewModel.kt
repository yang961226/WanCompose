package com.sundayting.wancompose.page.homescreen.mine.share

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
import com.sundayting.wancompose.page.homescreen.article.repo.toArticleUiBean
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.share.repo.MyCollectedArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyCollectedArticleViewModel @Inject constructor(
    private val eventManager: EventManager,
    private val repo: MyCollectedArticleRepository,
    private val articleRepo: ArticleRepository,
) : ViewModel() {

    val state = MyCollectedArticleState(repo.cachedArticleList)

    @Stable
    class MyCollectedArticleState(list: List<ArticleList.ArticleUiBean> = listOf()) {
        private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>().apply {
            addAll(list)
        }
        val articleList: List<ArticleList.ArticleUiBean> = _articleList

        var isLoadingMore by mutableStateOf(false)
        var canLoadMore by mutableStateOf(true)

        fun addArticleList(list: List<ArticleList.ArticleUiBean>) {
            _articleList.addAll(list)
        }

        fun removeArticle(id: Long) {
            _articleList.removeIf { it.id == id }
        }
    }

    init {
        viewModelScope.launch {
            eventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>().collect { event ->
                if (event.tryCollect) {
                    state.addArticleList(listOf(event.bean.copy(isCollect = true)))
                } else {
                    state.removeArticle(event.bean.id)
                }
            }
        }
    }


    private var page: Int = 0

    init {
        if (!repo.cachedArticleListSuccess) {
            loadMore()
        }
    }

    fun unCollectArticle(bean: ArticleList.ArticleUiBean) {
        viewModelScope.launch {
            if (articleRepo.unCollectArticle(bean.id).isSuccess()) {
                eventManager.emitCollectArticleEvent(bean, false)
            }
        }
    }

    private var loadJob: Job? = null
    fun loadMore() {
        if (loadJob?.isActive == true || !state.canLoadMore || repo.cachedArticleListSuccess) {
            return
        }
        loadJob = viewModelScope.launch(NetExceptionHandler) {
            state.isLoadingMore = true
            val result = repo.fetchCollectedArticle(page)
            if (result.isSuccess()) {
                repo.cachedArticleListSuccess = true
                val data = result.body.requireData()
                state.canLoadMore = data.curPage < data.pageCount
                val list = data.list.map { it.toArticleUiBean() }
                state.addArticleList(list)
                repo.cachedArticleList.addAll(list)
            }
        }.also {
            it.invokeOnCompletion {
                state.isLoadingMore = false
            }
        }
    }

}