package com.sundayting.wancompose.page.homescreen.article

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.okhttp.isNSuccess
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.repo.toArticleUiBean
import com.sundayting.wancompose.page.homescreen.article.repo.toBannerUiBean
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val repo: ArticleRepository,
) : ViewModel() {

    private val _articleList = mutableStateListOf<ArticleList.ArticleUiBean>()
    val articleList: List<ArticleList.ArticleUiBean> = _articleList

    private val _bannerList = mutableStateListOf<ArticleList.BannerUiBean>()
    val bannerList: List<ArticleList.BannerUiBean> = _bannerList

    var refreshing by mutableStateOf(false)
    var loadingMore by mutableStateOf(false)

    private var curPage = 0

    init {
        refresh()
    }

    private fun addArticle(list: List<ArticleList.ArticleUiBean>, refreshFirst: Boolean = false) {
        if (refreshFirst) {
            _articleList.clear()
        }
        _articleList.addAll(list)
    }

    private fun addTopArticle(list: List<ArticleList.ArticleUiBean>) {
        _articleList.addAll(0, list)
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
            refreshing = true
        } else {
            loadingMore = true
        }
        loadJob = viewModelScope.launch {
            joinAll(
                launch {
                    if (isRefresh) {
                        val result = repo.fetchHomePageBanner()
                        if (result.isSuccess() && result.body.data != null) {
                            _bannerList.clear()
                            _bannerList.addAll(result.body.data.map { it.toBannerUiBean() })
                        }
                    }
                },
                launch {
//                    val topArticleListDeferred = async {
//                        if (isRefresh) {
//                            val result = repo.fetchHomePageTopArticle()
//                            if (result.isSuccess() && result.body.data != null) {
//                                result.body.data
//                            } else {
//                                null
//                            }
//                        } else {
//                            null
//                        }
//                    }

                    val articleListDeferred = async {
                        val result = repo.fetchHomePageArticle(curPage)
                        if (result.isNSuccess()) {
                            result.body.data?.list
                        } else {
                            null
                        }
                    }

//                    val topArticleList = topArticleListDeferred.await()
                    val articleList = articleListDeferred.await()

                    if (articleList != null) {
                        addArticle(articleList.map { it.toArticleUiBean() }, isRefresh)
                    }
//                    if (topArticleList != null) {
//                        addTopArticle(topArticleList.map { it.toArticleUiBean(true) })
//                    }
                },
            )
        }.apply {
            invokeOnCompletion {
                refreshing = false
                loadingMore = false
            }
        }
    }

}