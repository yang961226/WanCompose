package com.sundayting.wancompose.page.search

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sundayting.wancompose.page.search.SearchViewModel.SearchItemType
import com.sundayting.wancompose.page.search.SearchViewModel.SearchItemType.Companion.TYPE_HISTORY
import com.sundayting.wancompose.page.search.SearchViewModel.SearchItemType.Companion.TYPE_HOT
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchItem(searchItem: List<SearchViewModel.SearchItem>)

    @Query("SELECT * FROM SearchItem WHERE userId IN (:uid) AND itemType IN(:itemType)")
    fun getUserSearchHistoryListFlow(
        uid: Long,
        @SearchItemType itemType: Int = TYPE_HISTORY,
    ): Flow<List<SearchViewModel.SearchItem>>

    @Query("SELECT * FROM SearchItem WHERE itemType IN(:itemType)")
    fun getSearchHotListFlow(
        @SearchItemType itemType: Int = TYPE_HOT,
    ): Flow<List<SearchViewModel.SearchItem>>

    @Query("DELETE FROM SearchItem  WHERE userId IN (:uid) AND itemType IN (:itemType)")
    suspend fun clearUserHistory(
        uid: Long,
        @SearchItemType itemType: Int = TYPE_HISTORY,
    )


}