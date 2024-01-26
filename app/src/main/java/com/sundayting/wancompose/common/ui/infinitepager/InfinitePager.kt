package com.sundayting.wancompose.common.ui.infinitepager

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.snapping.SnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private const val MAX_VALUE = 500000
private const val InfiniteInitialPage = MAX_VALUE / 2

@Composable
fun rememberInfiniteLoopPagerState(): PagerState {
    return rememberPagerState(
        initialPage = InfiniteInitialPage,
        pageCount = { MAX_VALUE }
    )
}

/**
 * 对HorizontalPager进行封装，实现无限滚动
 */
@Composable
fun InfiniteLoopHorizontalPager(
    modifier: Modifier = Modifier,
    realPageCount: Int,
    state: PagerState = rememberInfiniteLoopPagerState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pageSize: PageSize = PageSize.Fill,
    beyondBoundsPageCount: Int = 0,
    pageSpacing: Dp = 0.dp,
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    flingBehavior: SnapFlingBehavior = PagerDefaults.flingBehavior(state = state),
    userScrollEnabled: Boolean = true,
    reverseLayout: Boolean = false,
    key: ((index: Int) -> Any)? = null,
    pageNestedScrollConnection: NestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
        state = state,
        Orientation.Horizontal
    ),
    pageContent: @Composable (page: Int) -> Unit,
) {
    HorizontalPager(
        modifier = modifier,
        state = state,
        contentPadding = contentPadding,
        pageSize = pageSize,
        beyondBoundsPageCount = beyondBoundsPageCount,
        pageSpacing = pageSpacing,
        verticalAlignment = verticalAlignment,
        flingBehavior = flingBehavior,
        userScrollEnabled = userScrollEnabled,
        reverseLayout = reverseLayout,
        key = key,
        pageNestedScrollConnection = pageNestedScrollConnection
    ) { page ->
        pageContent((page - InfiniteInitialPage).myMod(realPageCount))
    }

}

fun PagerState.currentPageInInfinitePage(pageCount: Int): Int {
    return (currentPage - InfiniteInitialPage).myMod(pageCount)
}

fun PagerState.settledPageInInfinitePage(pageCount: Int): Int {
    return (settledPage - InfiniteInitialPage).myMod(pageCount)
}

fun PagerState.targetPageInInfinitePage(pageCount: Int): Int {
    return (targetPage - InfiniteInitialPage).myMod(pageCount)
}


private fun Int.myMod(other: Int): Int = when (other) {
    0 -> 0
    else -> this - floorDiv(other) * other
}

@Composable
@Preview
fun Test() {
    val pagerState = rememberPagerState {
        10000
    }
    HorizontalPager(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        state = pagerState,
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    if (it % 2 == 0) {
                        Color.Red
                    } else {
                        Color.Blue
                    }
                )
        )
    }
}