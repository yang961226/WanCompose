package com.sundayting.wancompose.page.homescreen.mine.share

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.homescreen.mine.share.repo.MyShareArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyShareArticleViewModel @Inject constructor(
    private val repo: MyShareArticleRepository,
) : ViewModel() {

    val state = MyShareArticleState()

    @Stable
    class MyShareArticleState {

    }


    private var page: Int = 0

    init {
        loadMore()
    }

    fun loadMore() {
        viewModelScope.launch {
            val result = repo.fetchCollectedArticle(page)
            if (result.isSuccess()) {

            }
        }
    }

}