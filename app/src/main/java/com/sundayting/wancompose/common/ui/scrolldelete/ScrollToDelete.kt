package com.sundayting.wancompose.common.ui.scrolldelete

import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sundayting.wancompose.R
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleListSingleBean
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

    ConstraintLayout(
        modifier.anchoredDraggable(state, Orientation.Horizontal),
    ) {
        Box(
            Modifier
                .constrainAs(createRef()) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                    end.linkTo(parent.end)
                }
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
                .constrainAs(createRef()) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
                .offset {
                    IntOffset(
                        x = state
                            .requireOffset()
                            .roundToInt(),
                        y = 0
                    )
                }
                .background(Color.Blue)
        ) {
            content()
        }

    }

}

enum class DragValue { IDLE, SHOW }

@Composable
@Preview(showBackground = true)
private fun PreviewScrollToDelete() {
    ScrollToDelete(Modifier.fillMaxWidth()) {
        ArticleListSingleBean(
            modifier = Modifier.fillMaxWidth(),
            bean = remember {
                ArticleList.ArticleUiBean(
                    envelopePic = null,
                    title = "我是标题我",
                    date = "1小时之前",
                    isNew = true,
                    isStick = true,
                    chapter = ArticleList.ArticleUiBean.Chapter(
                        superChapterName = "广场Tab",
                        chapterName = "自助"
                    ),
                    authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(
                        author = "小茗同学",
                    ),
                    id = 50,
                    isCollect = true,
                    desc = "我是描述我是",
                    tags = listOf(
                        ArticleList.ArticleUiBean.Tag(
                            name = "哈哈哈",
                            url = "134"
                        )
                    )
                )
            },
        )
    }
}