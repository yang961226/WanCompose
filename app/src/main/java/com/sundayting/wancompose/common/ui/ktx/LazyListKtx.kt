package com.sundayting.wancompose.common.ui.ktx

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow

@Composable
fun LazyListState.onBottomReached(
    loadMore: () -> Unit,
) {
    // state object which tells us if we should load more
    val shouldLoadMore by remember {
        derivedStateOf {

            // get last visible item
            val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()
                ?:
                // list is empty
                // return false here if loadMore should not be invoked if the list is empty
                return@derivedStateOf false

            // Check if last visible item is the last item in the list
            lastVisibleItem.index == layoutInfo.totalItemsCount - 1
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { shouldLoadMore }
            .collect {
                // if should load more, then invoke loadMore
                if (it) loadMore()
            }
    }
}