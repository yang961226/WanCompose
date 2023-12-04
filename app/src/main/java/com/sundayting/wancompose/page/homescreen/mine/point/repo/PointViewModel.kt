package com.sundayting.wancompose.page.homescreen.mine.point.repo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.network.requireData
import com.sundayting.wancompose.page.homescreen.mine.point.PointScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PointViewModel @Inject constructor(
    private val repo: PointRepository,
) : ViewModel() {

    val state = PointScreen.PointState()
    private var loadJob: Job? = null
    private var page: Int = 1

    init {
        loadMore()
    }

    fun loadMore() {
        if (loadJob?.isActive == true || state.canLoadMore.not()) {
            return
        }
        loadJob = viewModelScope.launch {
            state.isLoading = true
            val result = repo.fetchPointList(page)
            if (result.isSuccess()) {
                val pointData = result.body.requireData()
                page = pointData.curPage + 1
                state.addRecord(pointData.datas.map {
                    PointScreen.GetPointRecord(
                        title = it.reason,
                        date = it.date,
                        points = it.coinCount,
                        id = it.id
                    )
                })
                state.canLoadMore = pointData.curPage < pointData.pageCount
            }
        }.also {
            it.invokeOnCompletion { state.isLoading = false }
        }
    }

}