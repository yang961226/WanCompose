package com.sundayting.wancompose.page.examplewidgetscreen.nestscroll

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarProperties
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

object NestScroll : WanComposeDestination {
    override val route: String
        get() = "嵌套滑动"

    fun NavController.navigateToNestScroll() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(modifier: Modifier = Modifier, onClickBackButton: () -> Unit) {
        TitleBarWithContent(
            modifier,
            properties = TitleBarProperties(elevation = 0.dp),
            titleBarContent = {
                TitleBarWithBackButtonContent(
                    onClickBackButton = onClickBackButton
                ) {
                    Text(
                        "嵌套滑动",
                        style = TextStyle(
                            fontSize = 16.sp, color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                    )
                }
            }
        ) {
            val state = rememberPagerState {
                2
            }
            CustomNestScrollByMyself(Modifier.fillMaxSize())

        }
    }
}

@Composable
private fun NestScrollByCv(modifier: Modifier = Modifier) {

}

@Composable
private fun CustomNestScrollByMyself(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    var collapsingTopHeight by remember { mutableFloatStateOf(0f) }

    var offset by remember { mutableFloatStateOf(0f) }

    fun calculateOffset(delta: Float): Offset {
        val oldOffset = offset
        val newOffset = (oldOffset + delta).coerceIn(-collapsingTopHeight, 0f)
        offset = newOffset
        return Offset(0f, newOffset - oldOffset)
    }

    val scope = rememberCoroutineScope()

    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                if (source == NestedScrollSource.Drag) {
                    scope.launch {
                        scrollState.stopScroll()
                    }
                }
                return when {
                    available.y >= 0 -> Offset.Zero
                    offset == -collapsingTopHeight -> Offset.Zero
                    else -> calculateOffset(available.y)
                }
            }


            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset =
                when {
                    available.y <= 0 -> Offset.Zero
                    offset == 0f -> Offset.Zero
                    else -> calculateOffset(available.y)
                }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(nestedScrollConnection),
    ) {
        Box(
            modifier = Modifier
                .scrollable(
                    scrollState,
                    orientation = Orientation.Vertical,
                    reverseDirection = true
                )
                .onSizeChanged { size ->
                    collapsingTopHeight = size.height.toFloat()
                }
                .offset { IntOffset(x = 0, y = offset.roundToInt()) },
            content = {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .background(Color.Red)
                )
            },
        )
        Box(
            modifier = Modifier.offset {
                IntOffset(
                    x = 0,
                    y = (collapsingTopHeight + offset).roundToInt()
                )
            },
            content = {
                LazyColumn(Modifier.fillMaxWidth()) {
                    items(1000) {
                        Text("我是$it")
                    }
                }
            },
        )
    }
}