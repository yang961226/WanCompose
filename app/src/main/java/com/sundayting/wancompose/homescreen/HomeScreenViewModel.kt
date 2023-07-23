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
import io.ktor.resources.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val repo: ArticleRepository,
) : ViewModel() {

    @Stable
    class HomeScreenState {

        private val _articleList = mutableStateListOf<ArticleUiBean>()
        val articleList: List<ArticleUiBean> = _articleList

        var refreshing by mutableStateOf(false)
        var loadingMore by mutableStateOf(false)

        fun addArticle(list: List<ArticleUiBean>, refreshFirst: Boolean = false) {
            if (refreshFirst) {
                _articleList.clear()
            }
            _articleList.addAll(list)
        }

    }

    val homeScreenState = HomeScreenState()

    @Resource("/article")
    class Article {
        @Resource("list")
        class List(val parent: Article = Article()) {

            @Resource("{id}")
            class Id(val parent: List = List(), val id: Int) {
                @Resource("json")
                class Json(val parent: Id)
            }

        }
    }

    init {
        refresh()
    }

    private var loadJob: Job? = null

    fun refresh() {
        load(true)
    }

    fun loadMore() {
        load(false)
    }

    private fun load(isRefresh: Boolean) {
        loadJob?.cancel()
        if (isRefresh) {
            homeScreenState.refreshing = true
        } else {
            homeScreenState.loadingMore = true
        }
        loadJob = viewModelScope.launch {
            val result = runCatching {
                repo.fetchHomePageArticle(0)
            }
            result.onSuccess { bean ->
                bean.data?.let { data ->
                    homeScreenState.addArticle(data.list.map { it.toArticleUiBean() }, isRefresh)
                }
            }
        }.apply {
            invokeOnCompletion {
                homeScreenState.refreshing = false
                homeScreenState.loadingMore = false
            }
        }
    }

}