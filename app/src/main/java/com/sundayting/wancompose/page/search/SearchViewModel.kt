package com.sundayting.wancompose.page.search

import androidx.annotation.IntDef
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Entity
import com.sundayting.wancompose.common.event.ArticleCollectChangeEvent
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.toArticleUiBean
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import com.sundayting.wancompose.page.search.SearchViewModel.SearchItemType.Companion.TYPE_HISTORY
import com.sundayting.wancompose.page.search.SearchViewModel.SearchItemType.Companion.TYPE_HOT
import com.sundayting.wancompose.page.search.SearchViewModel.SearchUiState.SearchPageType.ResultPage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repo: SearchRepository,
    private val mineRepository: MineRepository,
    private val articleRepository: ArticleRepository,
    private val eventManager: EventManager,
) : ViewModel() {

    val uiState = SearchUiState()

    private var page = 0
    private var curKeyWord: String? = null

    init {
        viewModelScope.launch(SupervisorJob()) {
            launch {
                mineRepository.curUidFlow.flatMapLatest {
                    repo.getUserSearchHistoryListFlow(it)
                }.collect {
                    uiState.historySearchList.clear()
                    uiState.historySearchList.addAll(it.map { item -> item.text })
                }
            }
            launch {
                mineRepository.curUidFlow.flatMapLatest {
                    repo.getSearchHotListFlow()
                }.collect {
                    uiState.hotSearchList.clear()
                    uiState.hotSearchList.addAll(it.map { item -> item.text })
                }
            }
            launch {
                val result = repo.fetchHotSearch()
                if (result.isSuccess()) {
                    addSearchItem(
                        result.body.requireData().map { it.name },
                        TYPE_HOT
                    )
                }
            }
            launch {
                eventManager.eventFlow.filterIsInstance<ArticleCollectChangeEvent>()
                    .collect { event ->
                        val index =
                            uiState.articleList.indexOfFirst { it.id == event.bean.id }
                                .takeIf { it != -1 }
                                ?: return@collect

                        uiState.changeArticle(
                            index,
                            uiState.articleList[index].copy(isCollect = event.tryCollect)
                        )
                    }
            }
        }
    }

    private var loadMoreJob: Job? = null

    fun loadMore(
        keyWord: String? = null,
    ) {
        loadMoreJob?.cancel()

        loadMoreJob = viewModelScope.launch {
            if (keyWord != null && keyWord != curKeyWord) {
                page = 0
                uiState.clearArticleState()
                curKeyWord = keyWord
            }

            if (curKeyWord == null || !uiState.canLoadMore) {
                return@launch
            }

            uiState.isLoadingMore = true

            val result = articleRepository.searchArticle(
                page = page,
                curKeyWord!!
            )

            if (result.isSuccess()) {
                val data = result.body.requireData()
                uiState.canLoadMore = data.curPage <= data.pageCount
                uiState.articleList.addAll(data.datas.map { it.toArticleUiBean() })
                page = data.curPage + 1
            }
        }.apply {
            invokeOnCompletion { uiState.isLoadingMore = false }
        }
    }

    private var changeCollectJob: Job? = null

    fun collectOrUnCollectArticle(bean: ArticleList.ArticleUiBean) {
        if (mineRepository.curUserFlow.value == null && changeCollectJob?.isActive == true) {
            eventManager.emitEvent(ShowLoginPageEvent)
            return
        }
        changeCollectJob = viewModelScope.launch {
            if (!bean.isCollect) {
                if (articleRepository.collectArticle(bean.id).isSuccess()) {
                    eventManager.emitCollectArticleEvent(bean, true)
                }
            } else {
                if (articleRepository.unCollectArticle(bean.id).isSuccess()) {
                    eventManager.emitCollectArticleEvent(bean, false)
                }
            }
        }

    }

    private val mutex = Mutex()

    fun onSearchInputChanged(input: TextFieldValue) {
        uiState.searchInputString = input
    }

    fun onSearch(input: String? = null) {
        if (input != null) {
            uiState.searchInputString = TextFieldValue(input, selection = TextRange(input.length))
        }
        if (uiState.searchInputString.text.isEmpty()) {
            return
        }
        uiState.searchPageType = ResultPage
        addSearchItem(uiState.searchInputString.text, TYPE_HISTORY)
        loadMore(uiState.searchInputString.text)
    }

    fun clearHistory() {
        viewModelScope.launch {
            mutex.withLock {
                repo.clearUserHistory(mineRepository.curUidFlow.value)
            }
        }
    }

    private fun addSearchItem(list: List<String>, @SearchItemType itemType: Int) {
        viewModelScope.launch {
            mutex.withLock {
                repo.insertSearchItem(
                    list.map {
                        SearchItem(
                            text = it,
                            userId = mineRepository.curUidFlow.value,
                            itemType = itemType
                        )
                    }
                )
            }
        }
    }

    private fun addSearchItem(text: String, @SearchItemType itemType: Int) {
        viewModelScope.launch {
            mutex.withLock {
                repo.insertSearchItem(
                    listOf(
                        SearchItem(
                            text = text,
                            userId = mineRepository.curUidFlow.value,
                            itemType = itemType
                        )
                    )
                )
            }
        }
    }

    @Entity(
        primaryKeys = ["itemType", "text"]
    )
    data class SearchItem(
        val text: String,
        val userId: Long = 0L,

        @SearchItemType
        val itemType: Int,
    )


    @IntDef(TYPE_HISTORY, TYPE_HOT)
    annotation class SearchItemType {
        companion object {
            const val TYPE_HISTORY = 1
            const val TYPE_HOT = 2
        }
    }


    @Stable
    class SearchUiState {

        var searchInputString by mutableStateOf(TextFieldValue(""))

        enum class SearchPageType {
            TipsPage,
            ResultPage
        }

        var searchPageType by mutableStateOf(SearchPageType.TipsPage)

        val hotSearchList = mutableStateListOf<String>()
        val historySearchList = mutableStateListOf<String>()

        val articleList = mutableStateListOf<ArticleList.ArticleUiBean>()
        var isLoadingMore by mutableStateOf(false)
        var canLoadMore by mutableStateOf(false)

        fun clearArticleState() {
            articleList.clear()
            canLoadMore = true
        }

        fun changeArticle(index: Int, article: ArticleList.ArticleUiBean) {
            articleList[index] = article
        }

    }

}