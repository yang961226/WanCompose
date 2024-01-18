package com.sundayting.wancompose.page.homescreen

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private enum class Slot {
    Tabs,
    Indicator
}

data class MyTabPosition(val left: Dp, val width: Dp) {
    val right = left + width
}

@Stable
class MyScrollableTabState : ScrollableState {

    companion object {
        val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        )
    }

    data class ScrollableTabMeasureResult(
        val tabPositions: List<MyTabPosition>,
        val density: Density,
    )

    val currentTabIndex
        get() = currentTabIndexState.intValue

    val targetTabIndex by derivedStateOf {
        if (!isScrollInProgress) {
            currentTabIndex
        } else if (animateScrollToIndex != -1) {
            animateScrollToIndex
        } else {
            //这里还没做手动滑动的部分，暂时用currentTabIndex
            currentTabIndex
        }
    }

    private var animateScrollToIndex by mutableIntStateOf(-1)

    private var currentTabIndexState = mutableIntStateOf(0)

    internal val scrollState = ScrollState(0)

    override val isScrollInProgress: Boolean = scrollState.isScrollInProgress

    private var measureResult by mutableStateOf<ScrollableTabMeasureResult?>(null)

    suspend fun animateScrollToIndex(index: Int) {
        val curMeasureResult = measureResult
        if (index == currentTabIndex || curMeasureResult == null) {
            return
        }
        curMeasureResult.tabPositions.getOrNull(index)?.let {
            val calculatedOffset =
                it.calculateTabOffset(curMeasureResult.density, curMeasureResult.tabPositions)
            if (scrollState.value != calculatedOffset) {
                currentTabIndexState.intValue = index
                animateScrollToIndex = index
                scrollState.animateScrollTo(
                    calculatedOffset,
                    animationSpec = ScrollableTabRowScrollSpec
                )

            }
            animateScrollToIndex = -1
            currentTabIndexState.intValue = index
        }
    }

    override fun dispatchRawDelta(delta: Float): Float = scrollState.dispatchRawDelta(delta)

    fun onLaidOut(
        measureResult: ScrollableTabMeasureResult,
    ) {
        this.measureResult = measureResult
    }

    private fun MyTabPosition.calculateTabOffset(
        density: Density,
        tabPositions: List<MyTabPosition>,
    ): Int = with(density) {
        val totalTabRowWidth = tabPositions.last().right.roundToPx()
        val visibleWidth = totalTabRowWidth - scrollState.maxValue
        val scrollerCenter = visibleWidth / 2
        val tabOffset = left.roundToPx()
        val tabWidth = width.roundToPx()
        val centeredTabOffset = tabOffset - (scrollerCenter - tabWidth / 2)
        val availableSpace = (totalTabRowWidth - visibleWidth).coerceAtLeast(0)
        return centeredTabOffset.coerceIn(0, availableSpace)
    }

    override suspend fun scroll(
        scrollPriority: MutatePriority,
        block: suspend ScrollScope.() -> Unit,
    ) = scrollState.scroll(scrollPriority, block)

}

@Composable
fun MyScrollableTabRow2(
    modifier: Modifier = Modifier,
    state: MyScrollableTabState,
    indicator: @Composable () -> Unit,
    tabs: @Composable () -> Unit,
) {

    SubcomposeLayout(
        modifier
            .fillMaxWidth()
            .horizontalScroll(state.scrollState)
            .clipToBounds()
    ) { constraints ->
        val tabPlaceables =
            subcompose(Slot.Tabs, tabs).map { it.measure(constraints.copy(minWidth = 0)) }

        var totalWidth = 0
        tabPlaceables.forEach {
            totalWidth += it.width
        }
        val height = tabPlaceables.maxByOrNull { it.height }?.height ?: 0

        val indicatorPlaceables = subcompose(Slot.Indicator, indicator).map {
            it.measure(Constraints.fixed(totalWidth, height))
        }

        var left = 0
        layout(totalWidth, height) {
            val tabPositions = mutableListOf<MyTabPosition>()
            tabPlaceables.forEach {
                it.placeRelative(left, 0)
                tabPositions.add(MyTabPosition(left = left.toDp(), width = it.width.toDp()))
                left += it.width
            }
            indicatorPlaceables.forEach {
                it.placeRelative(0, 0)
            }

            state.onLaidOut(
                MyScrollableTabState.ScrollableTabMeasureResult(
                    tabPositions = tabPositions,
                    density = this@SubcomposeLayout
                )
            )
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewMyScrollableTabRow2() {
    val state = remember {
        MyScrollableTabState()
    }
    val scope = rememberCoroutineScope()

    Text("current:${state.currentTabIndex}  target:${state.targetTabIndex}")
    MyScrollableTabRow2(
        modifier = Modifier.padding(10.dp),
        state = state,
        indicator = {

        },
        tabs = {
            (0..10).forEach {
                Box(
                    Modifier
                        .padding(1.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (state.currentTabIndex == it) Color.Blue.copy(0.4f) else Color.Blue.copy(
                                0.2f
                            )
                        )
                        .height(50.dp)
                        .clickable {
                            scope.launch {
                                state.animateScrollToIndex(it)
                            }
                        }
                        .padding(horizontal = 10.dp), contentAlignment = Alignment.Center
                ) {
                    Text("我是第${it}")
                }
            }
        }
    )
}