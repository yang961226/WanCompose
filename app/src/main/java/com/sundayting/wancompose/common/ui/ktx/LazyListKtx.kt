package com.sundayting.wancompose.common.ui.ktx

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.map


data class LoadMoreDataWrapper(
    val totalItemNum: Int,
    val shouldLoadMore: Boolean,
)

@SuppressLint("ComposableNaming")
@Composable
fun LazyListState.onBottomReached(
    shouldLoadMoreWhenEmpty: Boolean = false,
    loadMore: () -> Unit,
) {
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?: return@derivedStateOf shouldLoadMoreWhenEmpty

            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            LoadMoreDataWrapper(
                totalItemNum = layoutInfo.totalItemsCount,
                shouldLoadMore = shouldLoadMore
            )
        }.map { it.shouldLoadMore }.collect {
            if (it) {
                loadMore()
            }
        }
    }
}