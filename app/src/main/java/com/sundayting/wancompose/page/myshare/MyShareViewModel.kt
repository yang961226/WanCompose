package com.sundayting.wancompose.page.myshare

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.NetExceptionHandler
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyShareViewModel @Inject constructor(
    private val repo: MyShareArticleRepository,
) : ViewModel() {

    val state = MyShareArticleUiState(repo.cachedArticleList)

    private var page: Int = 1

    private var loadJob: Job? = null
    fun loadMore() {
        if (loadJob?.isActive == true || !state.canLoadMore || repo.cachedArticleListSuccess) {
            return
        }
        loadJob = viewModelScope.launch(NetExceptionHandler) {
            state.isLoadingMore = true
            val result = repo.fetchSharedArticle(page)
            if (result.isSuccess()) {
//                repo.cachedArticleListSuccess = true
//                val data = result.body.requireData()
//                state.canLoadMore = data.curPage < data.pageCount
//                val list = data.list.map { it.toArticleUiBean() }
//                state.addArticleList(list)
//                repo.cachedArticleList.addAll(list)
            }
        }.also {
            it.invokeOnCompletion {
                state.isLoadingMore = false
            }
        }
    }


    @Stable
    class MyShareArticleUiState(list: List<ArticleList.ArticleUiBean> = listOf()) {

        var isLoading by mutableStateOf(false)
        var isLoadingMore by mutableStateOf(false)
        var canLoadMore by mutableStateOf(false)

        private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>().apply {
            addAll(list)
        }
        val articleList: List<ArticleList.ArticleUiBean> = _articleList

        fun addArticleList(list: List<ArticleList.ArticleUiBean>) {
            _articleList.addAll(list)
        }

    }


}