package com.sundayting.wancompose.page.search

import com.sundayting.wancompose.db.WanDatabase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    db: WanDatabase,
    private val searchService: SearchService,
) {

    suspend fun fetchHotSearch() = searchService.fetchHotSearch()

    private val dao = db.historyDao()

    suspend fun insertSearchItem(searchItem: List<SearchViewModel.SearchItem>) =
        dao.insertSearchItem(searchItem)

    fun getUserSearchHistoryListFlow(uid: Long) =
        dao.getUserSearchHistoryListFlow(uid)

    fun getSearchHotListFlow() = dao.getSearchHotListFlow()

    suspend fun clearUserHistory(uid: Long) = dao.clearUserHistory(uid)

}