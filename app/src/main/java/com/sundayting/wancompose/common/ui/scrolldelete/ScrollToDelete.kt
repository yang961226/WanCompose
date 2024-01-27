package com.sundayting.wancompose.common.ui.scrolldelete

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sundayting.wancompose.R
import com.sundayting.wancompose.theme.AlwaysLightModeArea
import com.sundayting.wancompose.theme.WanTheme
import kotlin.math.roundToInt

@Composable
fun rememberScrollToDeleteState(): AnchoredDraggableState<DragValue> {
    return remember {
        AnchoredDraggableState(
            initialValue = DragValue.IDLE,
            positionalThreshold = { distance: Float -> distance * 0.2f },
            velocityThreshold = { 1000f },
            animationSpec = tween()
        )
    }
}

@Composable
fun ScrollToDelete(
    modifier: Modifier = Modifier,
    state: AnchoredDraggableState<DragValue> = rememberScrollToDeleteState(),
    content: @Composable () -> Unit,
) {

    var deleteAreaWidth by remember {
        mutableIntStateOf(0)
    }

    val anchors = remember(deleteAreaWidth) {
        DraggableAnchors {
            DragValue.SHOW at -deleteAreaWidth.toFloat()
            DragValue.IDLE at 0f
        }
    }

    SideEffect {
        state.updateAnchors(anchors)
    }

    Box(
        modifier
            .height(IntrinsicSize.Min)
            .anchoredDraggable(state, Orientation.Horizontal),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .background(WanTheme.colors.primaryColor)
                .onSizeChanged { deleteAreaWidth = it.width },
            contentAlignment = Alignment.Center
        ) {
            AlwaysLightModeArea {
                Text(
                    text = stringResource(id = R.string.delete),
                    style = WanTheme.typography.h6.copy(
                        color = WanTheme.colors.level1TextColor
                    ),
                    modifier = Modifier.padding(horizontal = 15.dp)
                )
            }
        }

        Box(
            Modifier
                .offset {
                    IntOffset(
                        x = state
                            .requireOffset()
                            .roundToInt(),
                        y = 0
                    )
                }
        ) {
            content()
        }

    }

}

enum class DragValue { IDLE, SHOW }

@Composable
@Preview(showBackground = true)
private fun PreviewScrollToDelete() {
    ScrollToDelete(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(Color.Red)
        )
    }
}