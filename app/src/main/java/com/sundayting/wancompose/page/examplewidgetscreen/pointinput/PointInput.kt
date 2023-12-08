package com.sundayting.wancompose.page.examplewidgetscreen.pointinput

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.FractionalThreshold
import androidx.compose.material.Text
import androidx.compose.material.rememberSwipeableState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material.swipeable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.event.LocalEventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object PointInput : WanComposeDestination {
    override val route: String
        get() = "手势"

    fun NavController.navigateToPointInput() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(modifier: Modifier, onClickBackButton: () -> Unit) {
        TitleBarWithContent(
            modifier,
            properties = TitleBarProperties(elevation = 0.dp),
            titleBarContent = {
                TitleBarWithBackButtonContent(
                    onClickBackButton = onClickBackButton
                ) {
                    Text(
                        "手势",
                        style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        ) {
            Column(
                Modifier
                    .fillMaxSize()
            ) {
                val state = rememberPagerState { 5 }
                val scope = rememberCoroutineScope()
                CompositionLocalProvider(
                    LocalIndication provides rememberRipple()
                ) {
                    HorizontalPager(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f, false)
                            .padding(vertical = 5.dp),
                        state = state,
                        userScrollEnabled = false,
                        key = { it }
                    ) { page ->
                        when (page) {
                            0 -> ClickablePage(Modifier.fillMaxSize())
                            1 -> ScrollPage(Modifier.fillMaxSize())
                            2 -> DragPage(Modifier.fillMaxSize())
                            3 -> Transformable(Modifier.fillMaxSize())
                            4 -> CustomPointInput1(Modifier.fillMaxSize())
                        }
                    }
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .height(50.dp)
                            .padding(horizontal = 10.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        OptionItem(
                            modifier = Modifier.background(
                                if (state.currentPage == 0) Color.LightGray else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                            text = "高级Api：点击相关",
                            onClick = {
                                scope.launch {
                                    state.animateScrollToPage(0)
                                }
                            }
                        )
                        OptionItem(
                            modifier = Modifier.background(
                                if (state.currentPage == 1) Color.LightGray else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                            text = "高级Api：滚动相关",
                            onClick = {
                                scope.launch {
                                    state.animateScrollToPage(1)
                                }
                            }
                        )
                        OptionItem(
                            modifier = Modifier.background(
                                if (state.currentPage == 2) Color.LightGray else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                            text = "高级Api：拖动、滑动",
                            onClick = {
                                scope.launch {
                                    state.animateScrollToPage(2)
                                }
                            }
                        )
                        OptionItem(
                            modifier = Modifier.background(
                                if (state.currentPage == 3) Color.LightGray else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                            text = "多点平移缩放旋转",
                            onClick = {
                                scope.launch {
                                    state.animateScrollToPage(3)
                                }
                            }
                        )
                        OptionItem(
                            modifier = Modifier.background(
                                if (state.currentPage == 4) Color.LightGray else Color.Transparent,
                                shape = RoundedCornerShape(10.dp)
                            ),
                            text = "低级Api：自定义手势(1)",
                            onClick = {
                                scope.launch {
                                    state.animateScrollToPage(4)
                                }
                            }
                        )
                    }
                }

            }
        }

    }

    @Composable
    private fun OptionItem(
        modifier: Modifier = Modifier,
        text: String,
        onClick: () -> Unit,
    ) {
        Box(
            modifier
                .fillMaxHeight()
                .clickable { onClick() }
                .border(1.dp, Color.Black, shape = RoundedCornerShape(10.dp))
                .padding(2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text)
        }
    }

    @Composable
    private fun ClickablePage(
        modifier: Modifier = Modifier,
    ) {

        val eventManager = LocalEventManager.current

        Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val commonModifier = remember {
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            }
            PointInputItem(commonModifier, title = "clickable") {
                Box(
                    Modifier
                        .size(100.dp, 50.dp)
                        .background(Color.Green)
                        .clickable {
                            eventManager.emitToast("点击")
                        }
                )
            }
            PointInputItem(commonModifier, title = "combinedClickable") {
                Box(
                    Modifier
                        .size(100.dp, 50.dp)
                        .background(Color.Green)
                        .combinedClickable(
                            onClick = {
                                eventManager.emitToast("onClick")
                            },
                            onDoubleClick = {
                                eventManager.emitToast("onDoubleClick")
                            },
                            onLongClick = {
                                eventManager.emitToast("onLongClick")
                            }
                        )
                )
            }
            PointInputItem(commonModifier, title = "selectable") {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var isSelect by remember {
                            mutableStateOf(false)
                        }
                        Box(
                            Modifier
                                .size(100.dp, 50.dp)
                                .background(if (isSelect) Color.Red else Color.Green)
                                .selectable(
                                    selected = isSelect,
                                ) {
                                    isSelect = !isSelect
                                }
                        )
                        Text(if (isSelect) "选中" else "未选中")
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        var isSelect by remember {
                            mutableStateOf(false)
                        }
                        Box(
                            Modifier
                                .size(100.dp, 50.dp)
                                .background(if (isSelect) Color.Red else Color.Green)
                                .selectable(
                                    selected = isSelect,
                                ) {
                                    isSelect = !isSelect
                                }
                        )
                        Text(if (isSelect) "选中" else "未选中")
                    }
                }

            }
            PointInputItem(commonModifier, title = "toggleable") {
                var isChecked by remember {
                    mutableStateOf(false)
                }
                Box(
                    Modifier
                        .size(100.dp, 50.dp)
                        .background(if (isChecked) Color.Red else Color.Green)
                        .toggleable(value = isChecked, onValueChange = { isChecked = it })
                )
            }
        }

    }

    @Composable
    private fun ScrollPage(
        modifier: Modifier = Modifier,
    ) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val commonModifier = remember {
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            }
            PointInputItem(commonModifier, title = "horizontalScroll") {
                Row(
                    Modifier
                        .height(50.dp)
                        .horizontalScroll(rememberScrollState())
                ) {
                    repeat(20) {
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Red)
                        )
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Green)
                        )
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Blue)
                        )
                    }

                }
            }
            PointInputItem(commonModifier, title = "verticalScroll") {
                Column(
                    Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                ) {
                    repeat(20) {
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Red)
                        )
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Green)
                        )
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Blue)
                        )
                    }

                }
            }

            PointInputItem(commonModifier, title = "scrollable") {
                var offset by remember { mutableFloatStateOf(0f) }
                val scrollState = rememberScrollableState { delta ->
                    offset = (offset + delta).coerceIn(-150f, 150f)
                    delta
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            translationX = offset
                        }
                        .scrollable(scrollState, orientation = Orientation.Horizontal),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    repeat(20) {
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Red)
                        )
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Green)
                        )
                        Box(
                            Modifier
                                .size(50.dp)
                                .background(Color.Blue)
                        )
                    }

                }

                Text(offset.toString())
            }
        }
    }

    @Composable
    private fun DragPage(
        modifier: Modifier = Modifier,
    ) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val commonModifier = remember {
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            }
            PointInputItem(commonModifier, title = "draggable") {
                var max by remember { mutableStateOf(0.dp) }
                val min = 0.dp
                val (minPx, maxPx) = with(LocalDensity.current) { min.toPx() to max.toPx() }
                var offsetPosition by remember { mutableFloatStateOf(0f) }
                val state = rememberDraggableState { delta ->
                    val newValue = offsetPosition + delta
                    offsetPosition = newValue.coerceIn(minPx, maxPx)
                }
                val density = LocalDensity.current
                var curAnimatable: Animatable<Float, AnimationVector1D>? = null
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .draggable(
                            orientation = Orientation.Horizontal,
                            state = state,
//                            onDragStarted = {
//                                curAnimatable?.stop()
//                            },
//                            onDragStopped = { velocity ->
//                                state.drag {
//                                    var latestValue = offsetPosition
//                                    curAnimatable = Animatable(initialValue = latestValue).apply {
//                                        //滑动到边界的时候停止动画
//                                        updateBounds(minPx, maxPx)
//                                        //衰减停止
//                                        animateDecay(
//                                            initialVelocity = velocity,
//                                            animationSpec = splineBasedDecay(density)
//                                        ) {
//                                            dragBy(this.value - latestValue)
//                                            latestValue = this.value
//                                        }
//                                    }
//                                }
//                            }
                        )
                        .background(Color.Black)
                ) {
                    max = maxWidth - 50.dp
                    Box(
                        Modifier
                            .offset { IntOffset(offsetPosition.roundToInt(), 0) }
                            .size(50.dp)
                            .background(Color.Red)
                    )
                }
            }
            PointInputItem(commonModifier, title = "swipeable") {

                val squareSize = 48.dp
                val width = squareSize * 3

                val swipeableState = rememberSwipeableState(0)
                val sizePx = with(LocalDensity.current) { squareSize.toPx() }
                val anchors = mapOf(
                    sizePx * 0 to 0,
                    sizePx * 1 to 1,
                    sizePx * 2 to 2
                ) // Maps anchor points (in px) to states

                Column {
                    Box(
                        modifier = Modifier
                            .width(width)
                            .swipeable(
                                state = swipeableState,
                                anchors = anchors,
                                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                                orientation = Orientation.Horizontal
                            )
                            .background(Color.LightGray)
                    ) {
                        Box(
                            Modifier
                                .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
                                .size(squareSize)
                                .background(Color.DarkGray)
                        )
                    }
                    Text("当前位置：${swipeableState.currentValue}")
                }

            }
        }
    }

    @Composable
    private fun Transformable(
        modifier: Modifier = Modifier,
    ) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val commonModifier = remember {
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            }
            PointInputItem(commonModifier, title = "多点触摸") {
                fun Offset.rotateBy(angle: Float): Offset {
                    val angleInRadians = angle * PI / 180
                    return Offset(
                        (x * cos(angleInRadians) - y * sin(angleInRadians)).toFloat(),
                        (x * sin(angleInRadians) + y * cos(angleInRadians)).toFloat()
                    )
                }

                var offset by remember { mutableStateOf(Offset.Zero) }
                var zoom by remember { mutableFloatStateOf(1f) }
                var angle by remember { mutableFloatStateOf(0f) }

                Box(
                    Modifier
                        .pointerInput(Unit) {
                            detectTransformGestures(
                                onGesture = { centroid, pan, gestureZoom, gestureRotate ->
                                    val oldScale = zoom
                                    val newScale = zoom * gestureZoom

                                    // For natural zooming and rotating, the centroid of the gesture should
                                    // be the fixed point where zooming and rotating occurs.
                                    // We compute where the centroid was (in the pre-transformed coordinate
                                    // space), and then compute where it will be after this delta.
                                    // We then compute what the new offset should be to keep the centroid
                                    // visually stationary for rotating and zooming, and also apply the pan.
                                    offset =
                                        (offset + centroid / oldScale).rotateBy(gestureRotate) -
                                                (centroid / newScale + pan / oldScale)
                                    zoom = newScale
                                    angle += gestureRotate
                                }
                            )
                        }
                        .graphicsLayer {
                            translationX = -offset.x * zoom
                            translationY = -offset.y * zoom
                            scaleX = zoom
                            scaleY = zoom
                            rotationZ = angle
                            transformOrigin = TransformOrigin(0f, 0f)
                        }
                        .background(Color.Blue)
                        .fillMaxSize()
                )
            }
        }


    }

    @Composable
    private fun CustomPointInput1(
        modifier: Modifier = Modifier,
    ) {
        Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            val commonModifier = remember {
                Modifier
                    .fillMaxSize()
                    .weight(1f, false)
            }

            PointInputItem(commonModifier, title = "拖动") {
                val offsetX = remember { mutableFloatStateOf(0f) }
                val offsetY = remember { mutableFloatStateOf(0f) }
                var size by remember { mutableStateOf(Size.Zero) }
                Box(
                    Modifier
                        .fillMaxSize()
                        .onSizeChanged {
                            size = it.toSize()
                        }
                ) {
                    Box(
                        Modifier
                            .size(50.dp)
                            .offset {
                                IntOffset(
                                    offsetX.floatValue.roundToInt(),
                                    offsetY.floatValue.roundToInt()
                                )
                            }
                            .background(Color.Red)
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val original = Offset(offsetX.floatValue, offsetY.floatValue)
                                    val summed = original + dragAmount
                                    val newValue = Offset(
                                        x = summed.x.coerceIn(0f, size.width - 50.dp.toPx()),
                                        y = summed.y.coerceIn(0f, size.height - 50.dp.toPx())
                                    )
                                    offsetX.floatValue = newValue.x
                                    offsetY.floatValue = newValue.y
                                }
                            }
                    )
                }
            }
        }
    }

    @Composable
    private fun PointInputItem(
        modifier: Modifier = Modifier,
        title: String,
        content: @Composable BoxScope.() -> Unit,
    ) {
        Row(
            modifier
                .padding(horizontal = 10.dp)
                .border(1.dp, color = Color.Black, shape = RoundedCornerShape(10.dp))
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = title)
            }
            Divider(
                Modifier
                    .width(1.dp)
                    .fillMaxHeight()
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .weight(2f),
                contentAlignment = Alignment.Center
            ) {
                content()
            }
        }
    }

}