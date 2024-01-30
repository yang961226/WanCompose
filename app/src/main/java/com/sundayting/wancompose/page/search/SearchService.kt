package com.sundayting.wancompose.page.search

import com.sundayting.wancompose.network.NResult
import com.sundayting.wancompose.network.WanNResult
import de.jensklingenberg.ktorfit.http.GET
import kotlinx.serialization.Serializable

interface SearchService {

    @GET("hotkey/json")
    suspend fun fetchHotSearch(): NResult<SearchHotResult>

    @Serializable
    data class SearchHotResultItem(
        val name: String,
    )

    @Serializable
    data class SearchHotResult(
        override val data: List<SearchHotResultItem>?,
        override val errorCode: Int,
        override val errorMsg: String,
    ) : WanNResult<List<SearchHotResultItem>>()
}