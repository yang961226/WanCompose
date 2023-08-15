package com.sundayting.wancompose.page.homescreen.article

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val repo: ArticleRepository,
    private val mineRepo: MineRepository,
) : ViewModel() {

    val state = ArticleState()


    @Stable
    class ArticleState(
        list: List<ArticleList.ArticleUiBean> = listOf(),
    ) {

        var articleList by mutableStateOf(list)

        val bannerList = mutableStateListOf<ArticleList.BannerUiBean>()

        var refreshing by mutableStateOf(false)
        var loadingMore by mutableStateOf(false)

    }

    private var curPage = 0

    init {
        viewModelScope.launch {
            launch {
                mineRepo.curUserFlow.distinctUntilChangedBy { it?.id }.collect {
                    refresh()
                }
            }
            launch {
                mineRepo.curUserFlow.map { it?.id ?: VISITOR_ID }.flatMapLatest { id ->
                    repo.userArticleFlow(id).mapLatest { articleList ->
                        articleList.map { it.toArticleUiBean() }
                    }
                }.collectLatest {
                    state.articleList = it
                }
            }
        }

    }

    private suspend fun addArticle(list: List<ArticleBean>, refreshFirst: Boolean = false) {
        if (refreshFirst) {
            repo.deleteUsersArticleFlow(mineRepo.curUserFlow.firstOrNull()?.id ?: VISITOR_ID)
        }
        repo.insertArticles(list)
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
            state.loadingMore = true
        } else {
            state.loadingMore = true
        }
        loadJob = viewModelScope.launch(NetExceptionHandler + SupervisorJob()) {
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
                    addArticle(topArticleList + articleList, isRefresh)
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