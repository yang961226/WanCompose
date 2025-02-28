//package com.sundayting.wancompose.page.examplewidgetscreen.nestscroll
//
//import androidx.compose.foundation.MutatePriority
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.Orientation
//import androidx.compose.foundation.gestures.ScrollScope
//import androidx.compose.foundation.gestures.ScrollableState
//import androidx.compose.foundation.gestures.scrollable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.Stable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableIntStateOf
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.geometry.Offset
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
//import androidx.compose.ui.input.nestedscroll.NestedScrollSource
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import androidx.compose.ui.layout.Layout
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.compose.ui.zIndex
//import androidx.navigation.NavController
//import com.sundayting.wancompose.R
//import com.sundayting.wancompose.WanComposeDestination
//import com.sundayting.wancompose.common.ui.title.TitleBarProperties
//import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
//import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
//import com.sundayting.wancompose.page.examplewidgetscreen.ExampleCardBean
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import kotlin.math.abs
//import kotlin.math.max
//import kotlin.math.roundToInt
//
//object NestScroll : WanComposeDestination {
//    override val route: String
//        get() = "嵌套滑动"
//
//    val exampleCardBean = ExampleCardBean(
//        "嵌套滑动",
//        resId = R.drawable.ic_nest_scroll
//    )
//
//    fun NavController.navigateToNestScroll() {
//        navigate(route) {
//            launchSingleTop = true
//        }
//    }
//
//    @Composable
//    fun Screen(modifier: Modifier = Modifier, onClickBackButton: () -> Unit) {
//        TitleBarWithContent(
//            modifier,
//            properties = TitleBarProperties(elevation = 0.dp),
//            titleBarContent = {
//                TitleBarWithBackButtonContent(
//                    onClickBackButton = onClickBackButton
//                ) {
//                    Text(
//                        "嵌套滑动",
//                        style = TextStyle(
//                            fontSize = 16.sp, color = Color.White
//                        ),
//                        modifier = Modifier
//                            .align(Alignment.Center)
//                    )
//                }
//            }
//        ) {
//            var isRefreshing by remember { mutableStateOf(false) }
//            val scope = rememberCoroutineScope()
////            val pullRefreshState = rememberPullRefreshState(
////                refreshing = isRefreshing,
////                onRefresh = {
////                    scope.launch {
////                        isRefreshing = true
////                        delay(2000)
////                        isRefreshing = false
////                    }
////                })
//            val nestScrollState = remember { CollapsingToolbarScrollState() }
//            Text("${nestScrollState.value}", modifier = Modifier.zIndex(100f))
//            Box(Modifier.fillMaxSize()) {
//
//                NestScroll2(
//                    modifier = Modifier.pullRefresh(pullRefreshState),
//                    state = nestScrollState,
//                    toolBar = {
//                        Box(Modifier.height(10.dp))
//                        Box(
//                            Modifier
//                                .fillMaxWidth()
//                                .height(300.dp)
//                                .background(Color.LightGray)
//                                .verticalScroll(rememberScrollState()),
//                            contentAlignment = Alignment.Center
//                        ) {
//                            Text("滑我")
//                        }
//                    },
//                    body = {
//                        LazyColumn(
//                            Modifier
//                                .fillMaxSize()
//                        ) {
//                            items(100) {
//                                Text("我是第:${it}")
//                            }
//                        }
//                    })
//
//                PullRefreshIndicator(
//                    modifier = Modifier.align(Alignment.TopCenter),
//                    refreshing = isRefreshing,
//                    state = pullRefreshState
//                )
//            }
//
//
//        }
//    }
//}
//
//@Stable
//class CollapsingToolbarScrollState : ScrollableState {
//
//    var value by mutableIntStateOf(0)
//        private set
//
//    var maxValue: Int
//        get() = maxValueState
//        internal set(newValue) {
//            maxValueState = newValue
//            if (abs(newValue) < abs(value)) {
//                value = -newValue
//            }
//        }
//
//
//    private var maxValueState by mutableIntStateOf(Int.MAX_VALUE)
//
//    private var accumulator: Float = 0f
//
//    private val scrollableState = ScrollableState {
//        val absolute = (value + it + accumulator)
//        val newValue = absolute.coerceIn(-maxValueState.toFloat(), 0f)
//        val changed = absolute != newValue
//        val consumed = newValue - value
//        val consumedInt = consumed.roundToInt()
//        value += consumedInt
//        accumulator = consumed - consumedInt
//
//        // Avoid floating-point rounding error
//        if (changed) consumed else it
//    }
//
//    override val isScrollInProgress: Boolean
//        get() = scrollableState.isScrollInProgress
//
//    override fun dispatchRawDelta(delta: Float): Float = scrollableState.dispatchRawDelta(delta)
//
//    override suspend fun scroll(
//        scrollPriority: MutatePriority,
//        block: suspend ScrollScope.() -> Unit,
//    ) = scrollableState.scroll(scrollPriority, block)
//
//}
//
//@Composable
//private fun NestScroll2(
//    modifier: Modifier = Modifier,
//    state: CollapsingToolbarScrollState,
//    toolBar: @Composable () -> Unit,
//    body: @Composable () -> Unit,
//) {
//    Layout(
//        modifier = modifier.nestedScroll(remember {
//            object : NestedScrollConnection {
//                override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
//                    val deltaY = available.y
//                    if (deltaY < 0) {
//                        return Offset(0f, state.dispatchRawDelta(deltaY))
//                    }
//                    return Offset.Zero
//                }
//
//                override fun onPostScroll(
//                    consumed: Offset,
//                    available: Offset,
//                    source: NestedScrollSource,
//                ): Offset {
//                    val deltaY = available.y
//                    if (deltaY > 0) {
//                        return Offset(0f, state.dispatchRawDelta(deltaY))
//                    }
//                    return Offset.Zero
//                }
//            }
//        }),
//        contents = listOf(toolBar, body),
//        measurePolicy = { measurableLists, constraints ->
//
//            val toolbarMeasurables = measurableLists[0]
//            val bodyMeasurables = measurableLists[1]
//
//            val toolbarPlaceables =
//                toolbarMeasurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
//
//            val bodyPlaceables =
//                bodyMeasurables.map { it.measure(constraints.copy(minWidth = 0, minHeight = 0)) }
//
//            val toolbarMaxHeight = toolbarPlaceables.maxOfOrNull { it.height }
//                ?: 0
//
//            val toolbarMinHeight =
//                if (toolbarPlaceables.size == 1) 0 else toolbarPlaceables.minOfOrNull { it.height }
//                    ?: 0
//
//            val bodyHeight = bodyPlaceables.maxOfOrNull { it.height } ?: 0
//
//            val layoutWidth = max(toolbarPlaceables.maxOfOrNull { it.width } ?: 0,
//                bodyPlaceables.maxOfOrNull { it.width } ?: 0
//            )
//
//            val layoutHeight = max(toolbarMaxHeight, bodyHeight)
//
//            state.maxValue = toolbarMaxHeight - toolbarMinHeight
//
//            layout(layoutWidth, layoutHeight) {
//                bodyPlaceables.forEach { placeable ->
//                    placeable.placeRelative(0, toolbarMaxHeight + state.value)
//                }
//                toolbarPlaceables.forEach { placeable ->
//                    placeable.placeRelative(0, state.value)
//                }
//            }
//        }
//    )
//
//}
//
//@Composable
//@Preview(showBackground = true)
//private fun PreviewNestScroll() {
//    val state = remember { CollapsingToolbarScrollState() }
//    Text("${state.value}", modifier = Modifier.zIndex(100f))
//    NestScroll2(
//        state = state,
//        toolBar = {
//            Box(
//                Modifier
//                    .fillMaxWidth()
//                    .height(300.dp)
//                    .background(Color.LightGray)
//                    .scrollable(
//                        rememberScrollState(),
//                        orientation = Orientation.Vertical,
//                        reverseDirection = true
//                    ),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("哈哈哈")
//            }
//
//        },
//        body = {
//            LazyColumn {
//                items(100) {
//                    Text("我是$it", modifier = Modifier.fillMaxWidth())
//                }
//            }
//        }
//    )
//}