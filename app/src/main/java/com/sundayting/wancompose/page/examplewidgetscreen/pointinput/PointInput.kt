package com.sundayting.wancompose.page.examplewidgetscreen.pointinput

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.event.LocalEventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import kotlinx.coroutines.launch

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
                val state = rememberPagerState { 2 }
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
                            text = "高级Api：拖动相关",
                            onClick = {
                                scope.launch {
                                    state.animateScrollToPage(1)
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