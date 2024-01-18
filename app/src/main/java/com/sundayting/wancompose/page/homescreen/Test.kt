package com.sundayting.wancompose.page.homescreen

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

private enum class Slot {
    Tabs,
    Indicator
}

data class MyTabPosition(val left: Dp, val width: Dp) {
    val right = left + width
}

private class MyScrollableTabData(
    private val scrollState: ScrollState,
    private val coroutineScope: CoroutineScope,
) {

    companion object {
        private val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        )
    }

    private var selectedTab: Int? = null

    fun MyTabPosition.calculateTabOffset(
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

    fun onLaidOut(
        density: Density,
        tabPositions: List<MyTabPosition>,
        selectedTab: Int,
    ) {
        // Animate if the new tab is different from the old tab, or this is called for the first
        // time (i.e selectedTab is `null`).
        if (this.selectedTab != selectedTab) {
            this.selectedTab = selectedTab
            tabPositions.getOrNull(selectedTab)?.let {
                // Scrolls to the tab with [tabPosition], trying to place it in the center of the
                // screen or as close to the center as possible.
                val calculatedOffset = it.calculateTabOffset(density, tabPositions)
                if (scrollState.value != calculatedOffset) {
                    coroutineScope.launch {
                        scrollState.animateScrollTo(
                            calculatedOffset,
                            animationSpec = ScrollableTabRowScrollSpec
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun MyScrollableTabRow(
    modifier: Modifier = Modifier,
    selectedTabIndex: Int,
    indicator: @Composable () -> Unit,
    tabs: @Composable () -> Unit,
) {

    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val scrollableTabData = remember(scrollState, coroutineScope) {
        MyScrollableTabData(
            scrollState = scrollState,
            coroutineScope = coroutineScope
        )
    }
    Text("value:${scrollState.value}  maxValue:${scrollState.maxValue}")
    SubcomposeLayout(
        modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
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

            scrollableTabData.onLaidOut(
                density = this@SubcomposeLayout,
                tabPositions = tabPositions,
                selectedTab = selectedTabIndex
            )
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewMyScrollableTabRow() {

    var selectedIndex by remember { mutableIntStateOf(0) }
    MyScrollableTabRow(
        modifier = Modifier.padding(10.dp),
        selectedTabIndex = selectedIndex,
        indicator = {

        },
        tabs = {
            (0..10).forEach {
                Box(
                    Modifier
                        .padding(1.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (selectedIndex == it) Color.Blue.copy(0.4f) else Color.Blue.copy(
                                0.2f
                            )
                        )
                        .height(50.dp)
                        .clickable { selectedIndex = it }
                        .padding(horizontal = 10.dp), contentAlignment = Alignment.Center
                ) {
                    Text("我是第${it}")
                }
            }
        }
    )
}