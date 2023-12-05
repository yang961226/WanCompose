package com.sundayting.wancompose.page.homescreen.mine.share

import android.annotation.SuppressLint
import android.content.Context
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
import com.sundayting.wancompose.page.homescreen.mine.share.repo.MyShareArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class MyShareArticleViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repo: MyShareArticleRepository,
    private val articleRepo: ArticleRepository,
) : ViewModel() {

    val state = MyShareArticleState()

    @Stable
    class MyShareArticleState {
        private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>()
        val articleList: List<ArticleList.ArticleUiBean> = _articleList

        var isLoadingMore by mutableStateOf(false)
        var canLoadMore by mutableStateOf(true)

        fun addArticleList(list: List<ArticleList.ArticleUiBean>) {
            _articleList.addAll(list)
        }
    }

    init {
        viewModelScope.launch {
            EventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>().collect { event ->
                val article = state.articleList.firstOrNull { it.id == event.id } ?: return@collect
                article.isCollect = event.isCollect
            }
        }
    }


    private var page: Int = 0

    init {
        loadMore()
    }

    fun collectArticle(id: Long, isCollect: Boolean) {
        viewModelScope.launch {
            if (isCollect) {
                if (articleRepo.collectArticle(id).isSuccess()) {
                    EventManager.emitCollectArticleEvent(context, id, true)
                }
            } else {
                if (articleRepo.unCollectArticle(id).isSuccess()) {
                    EventManager.emitCollectArticleEvent(context, id, false)
                }
            }
        }
    }

    private var loadJob: Job? = null
    fun loadMore() {
        if (loadJob?.isActive == true || !state.canLoadMore) {
            return
        }
        loadJob = viewModelScope.launch(NetExceptionHandler) {
            val result = repo.fetchCollectedArticle(page)
            if (result.isSuccess()) {
                val data = result.body.requireData()
                state.canLoadMore = data.curPage < data.pageCount
                state.addArticleList(data.list.map { it.toArticleUiBean() })
            }
        }
    }

}