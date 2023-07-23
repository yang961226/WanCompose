package com.sundayting.wancompose.homescreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.homescreen.article.repo.toArticleUiBean
import com.sundayting.wancompose.homescreen.article.ui.ArticleList.ArticleUiBean
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repo: ArticleRepository,
) : ViewModel() {

    @Stable
    inner class ArticleListState {

        private val _articleList = mutableStateListOf<ArticleUiBean>()
        val articleList: List<ArticleUiBean> = _articleList

        var refreshing by mutableStateOf(false)
        var loadingMore by mutableStateOf(false)

        private var curPage = 0

        private fun addArticle(list: List<ArticleUiBean>, refreshFirst: Boolean = false) {
            if (refreshFirst) {
                _articleList.clear()
            }
            _articleList.addAll(list)
        }

        fun refresh() {
            load(true)
        }

        fun loadMore() {
            load(false)
        }

        private var loadJob: Job? = null

        private fun load(isRefresh: Boolean) {
            if (isRefresh.not() && loadJob?.isActive == true) {
                return
            }
            loadJob?.cancel()
            if (isRefresh) {
                curPage = 0
                articleListState.refreshing = true
            } else {
                articleListState.loadingMore = true
            }
            loadJob = viewModelScope.launch {
                val result = runCatching {
                    repo.fetchHomePageArticle(curPage)
                }
                result.onSuccess { bean ->
                    curPage++
                    bean.data?.let { data ->
                        articleListState.addArticle(
                            data.list.map { it.toArticleUiBean() },
                            isRefresh
                        )
                    }
                }
            }.apply {
                invokeOnCompletion {
                    articleListState.refreshing = false
                    articleListState.loadingMore = false
                }
            }
        }

    }

    val articleListState = ArticleListState()

    init {
        articleListState.refresh()
    }


}