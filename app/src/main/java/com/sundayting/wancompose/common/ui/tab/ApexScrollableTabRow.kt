package com.sundayting.wancompose.common.ui.tab

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.MutatePriority
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.dp
import com.sundayting.wancompose.common.ui.tab.Slot.Tabs
import kotlinx.coroutines.launch

private enum class Slot {
    Tabs,
    Indicator
}

data class ApexTabPosition(val left: Dp, val width: Dp) {
    val right = left + width
}

private val DefaultHorizontalSpaceGetter: (ApexTabPosition) -> Dp = {
    0.dp
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: ApexTabPosition,
    horizontalSpaceGetter: (ApexTabPosition) -> Dp = DefaultHorizontalSpaceGetter,
): Modifier = composed {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(
            durationMillis = ApexScrollableTabState.ScrollableTabRowDuration,
            easing = FastOutSlowInEasing
        ), label = ""
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(
            durationMillis = ApexScrollableTabState.ScrollableTabRowDuration,
            easing = FastOutSlowInEasing
        ), label = ""
    )
    val space = horizontalSpaceGetter(currentTabPosition)
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset + space)
        .width((currentTabWidth - space * 2).coerceAtLeast(0.dp))
}

@Composable
fun rememberApexScrollableTabState(): ApexScrollableTabState {
    return rememberSaveable(saver = ApexScrollableTabState.Saver) {
        ApexScrollableTabState()
    }
}

@Stable
class ApexScrollableTabState private constructor(curIndex: Int, init: Float) :
    ScrollableState {

    constructor(curIndex: Int = 0) : this(curIndex = curIndex, init = 0f)

    data class ScrollableTabMeasureResult(
        val tabPositions: List<ApexTabPosition>,
        val density: Density,
    )

    companion object {

        val Saver: Saver<ApexScrollableTabState, *> = listSaver(
            save = {
                listOf(
                    it.currentTabIndex,
                    it.scrollState.value
                )
            },
            restore = {
                ApexScrollableTabState(
                    curIndex = it[0],
                    init = it[1].toFloat()
                )
            }
        )

        const val ScrollableTabRowDuration = 250
        val ScrollableTabRowScrollSpec: AnimationSpec<Float> = tween(
            durationMillis = ScrollableTabRowDuration,
            easing = FastOutSlowInEasing
        )
    }


    internal val scrollState = ScrollState(init.toInt())

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

    private var currentTabIndexState = mutableIntStateOf(curIndex)

    override val isScrollInProgress: Boolean = scrollState.isScrollInProgress

    private var measureResult by mutableStateOf<ScrollableTabMeasureResult?>(null)

    suspend fun animateScrollToIndex(
        index: Int,
        animationSpec: AnimationSpec<Float> = ScrollableTabRowScrollSpec,
    ) {
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
                    animationSpec = animationSpec
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

    private fun ApexTabPosition.calculateTabOffset(
        density: Density,
        tabPositions: List<ApexTabPosition>,
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
fun ApexScrollableTabRow(
    modifier: Modifier = Modifier,
    alignment: Alignment.Vertical = Alignment.CenterVertically,
    state: ApexScrollableTabState,
    indicator: @Composable (List<ApexTabPosition>) -> Unit,
    horizontalSpacing: Dp = 0.dp,
    contentPaddingValues: PaddingValues = PaddingValues(0.dp),
    tabs: @Composable () -> Unit,
) {
    SubcomposeLayout(
        modifier
            .fillMaxWidth()
            .horizontalScroll(state.scrollState)
            .clipToBounds()
    ) { constraints ->
        val tabPlaceables =
            subcompose(Tabs, tabs).map { it.measure(constraints.copy(minWidth = 0)) }

        val horizontalSpacingPx = horizontalSpacing.roundToPx()

        val startContentPadding = contentPaddingValues.calculateStartPadding(layoutDirection)

        val endContentPadding = contentPaddingValues.calculateEndPadding(
            layoutDirection
        )

        var totalWidth =
            (startContentPadding + endContentPadding).roundToPx()
        tabPlaceables.forEachIndexed { index, placeable ->
            if (index != 0) {
                totalWidth += horizontalSpacingPx
            }
            totalWidth += placeable.width
        }
        val height = tabPlaceables.maxByOrNull { it.height }?.height ?: 0

        var left = 0
        layout(totalWidth, height) {
            val tabPositions = mutableListOf<ApexTabPosition>()
            tabPlaceables.forEachIndexed { index, placeable ->
                left += if (index != 0) {
                    horizontalSpacingPx
                } else {
                    startContentPadding.roundToPx()
                }
                placeable.placeRelative(
                    left, alignment.align(
                        size = placeable.height,
                        space = height
                    )
                )
                tabPositions.add(
                    ApexTabPosition(
                        left = left.toDp(),
                        width = placeable.width.toDp()
                    )
                )
                left += placeable.width
            }

            subcompose(Slot.Indicator) {
                indicator(tabPositions)
            }.forEach {
                it.measure(Constraints.fixed(totalWidth, height)).placeRelative(0, 0)
            }

            state.onLaidOut(
                ApexScrollableTabState.ScrollableTabMeasureResult(
                    tabPositions = tabPositions,
                    density = this@SubcomposeLayout
                ),
            )
        }
    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewApexScrollableTabRow() {
    val tabState = rememberApexScrollableTabState()
    val horizontalPagerState = rememberPagerState { 10 }
    val scope = rememberCoroutineScope()

    var quickSelect by remember { mutableStateOf(false) }
    val isDragged by horizontalPagerState.interactionSource.collectIsDraggedAsState()

    LaunchedEffect(Unit) {
        launch {
            snapshotFlow {
                Triple(
                    isDragged,
                    quickSelect,
                    horizontalPagerState.currentPage
                )
            }.collect {
                if (!it.second) {
                    tabState.animateScrollToIndex(it.third)
                }
            }
        }
        launch {
            snapshotFlow { isDragged }.collect { quickSelect = false }
        }
    }
    Column(
        Modifier
            .padding(top = 20.dp)
            .fillMaxSize()
    ) {
        ApexScrollableTabRow(
            alignment = Alignment.CenterVertically,
            state = tabState,
            horizontalSpacing = 15.dp,
            contentPaddingValues = PaddingValues(start = 20.dp, end = 30.dp),
            indicator = {
                Box(
                    Modifier
                        .tabIndicatorOffset(it[tabState.currentTabIndex], horizontalSpaceGetter = {
                            10.dp
                        })
                        .height(5.dp)
                        .background(Color.Red, shape = RoundedCornerShape(50))
                )
            },
            tabs = {
                (0..9).forEach {
                    val isSelect = tabState.currentTabIndex == it
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue.copy(0.2f))
                            .height(50.dp)
                            .clickable {
                                scope.launch {
                                    quickSelect = true
                                    launch {
                                        tabState.animateScrollToIndex(it)
                                    }
                                    launch {
                                        horizontalPagerState.animateScrollToPage(
                                            it, animationSpec = tween(
                                                durationMillis = ApexScrollableTabState.ScrollableTabRowDuration,
                                                easing = FastOutSlowInEasing
                                            )
                                        )
                                    }

                                }
                            }
                            .padding(horizontal = 10.dp + it.dp * 6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "我是第${it}", style = TextStyle(
                                color = if (isSelect) Color.Red.copy(0.7f) else Color.Black,
                                fontWeight = if (isSelect) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f, false)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background((if (it % 2 == 0) Color.Red else Color.Blue).copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text("我是第$it")
            }
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewApexScrollableTabRow2() {
    val tabState = rememberApexScrollableTabState()
    val horizontalPagerState = rememberPagerState { 10 }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        snapshotFlow { horizontalPagerState.currentPage }.collect {
            scope.launch {
                tabState.animateScrollToIndex(it)
            }
        }
    }
    Column(
        Modifier
            .padding(top = 20.dp)
            .fillMaxSize()
    ) {
        ApexScrollableTabRow(
            alignment = Alignment.CenterVertically,
            state = tabState,
            horizontalSpacing = 15.dp,
            contentPaddingValues = PaddingValues(start = 20.dp, end = 30.dp),
            indicator = {
                Box(
                    Modifier
                        .tabIndicatorOffset(it[tabState.currentTabIndex], horizontalSpaceGetter = {
                            10.dp
                        })
                        .height(5.dp)
                        .background(Color.Red, shape = RoundedCornerShape(50))
                )
            },
            tabs = {
                (0..9).forEach {
                    val isSelect = tabState.currentTabIndex == it
                    Box(
                        Modifier
                            .clip(RoundedCornerShape(50))
                            .background(Color.Blue.copy(0.2f))
                            .height(50.dp)
                            .clickable {
                                scope.launch {
                                    horizontalPagerState.animateScrollToPage(
                                        it, animationSpec = tween(
                                            durationMillis = ApexScrollableTabState.ScrollableTabRowDuration,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                }
                            }
                            .padding(horizontal = 10.dp + it.dp * 6),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "我是第${it}", style = TextStyle(
                                color = if (isSelect) Color.Red.copy(0.7f) else Color.Black,
                                fontWeight = if (isSelect) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }
        )

        Spacer(Modifier.height(10.dp))

        HorizontalPager(
            state = horizontalPagerState,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f, false)
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background((if (it % 2 == 0) Color.Red else Color.Blue).copy(0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text("我是第$it")
            }
        }

    }
}