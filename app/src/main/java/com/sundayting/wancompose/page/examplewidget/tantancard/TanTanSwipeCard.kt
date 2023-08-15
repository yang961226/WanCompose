package com.sundayting.wancompose.page.examplewidget.tantancard

import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlin.math.absoluteValue


/**
 * 探探用户对象实体类
 */
data class TanTanUserBean(
    val name: String,
    val picList: List<String>,
    val basicDetail: BasicDetail,
    val recentPost: RecentPost,
) {

    data class BasicDetail(
        val isMale: Boolean = false,
        val age: Int? = null,
        val tagList: List<Tag> = emptyList(),
    ) {

        data class Tag(
            @DrawableRes
            val icon: Int? = null,
            val content: String,
        )

    }

    data class RecentPost(
        val level: Int,
        val picList: List<String> = emptyList(),
    )

}

/**
 * 探探Like滑动卡
 */
@Composable
fun TanTanSwipeCard(
    modifier: Modifier = Modifier,
) {
}

@Composable
private fun TanTanSingleCard(
    modifier: Modifier = Modifier,
    userBean: TanTanUserBean,
) {

    var yRotateTag by remember { mutableFloatStateOf(0f) }

    val yRotateAnimate = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        snapshotFlow { yRotateTag }.drop(1).collectLatest {
            yRotateAnimate.animateTo(
                0f, animationSpec = spring(
                    stiffness = Spring.StiffnessMedium,
                ), initialVelocity = if (it > 0) 200f else -200f
            )
        }
    }

    ConstraintLayout(
        modifier
            .graphicsLayer {
                rotationY = yRotateAnimate.value
            }
            .clip(RoundedCornerShape(15.dp))
            .background(Color.Gray)
    ) {
        val (
            picContent,
            topIndicatorContent,
            userDetailContent,
        ) = createRefs()

        var indicatorIndex by remember(userBean.picList.size) { mutableIntStateOf(0) }

        val centerGuideLine = createGuidelineFromStart(0.5f)

        AsyncImage(
            modifier = Modifier
                .padding(bottom = 40.dp)
                .constrainAs(picContent) {
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                    centerTo(parent)
                },
            model = ImageRequest.Builder(LocalContext.current)
                .build(),
            contentDescription = null,
        )

        Box(
            Modifier
                .constrainAs(createRef()) {
                    start.linkTo(parent.start)
                    end.linkTo(centerGuideLine)
                    centerVerticallyTo(parent)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (indicatorIndex == 0) {
                        yRotateTag = -(yRotateTag.absoluteValue + 0.1f)
                        return@clickable
                    }
                    indicatorIndex--
                }
        )

        Box(
            Modifier
                .constrainAs(createRef()) {
                    start.linkTo(centerGuideLine)
                    end.linkTo(parent.end)
                    centerVerticallyTo(parent)
                    width = Dimension.fillToConstraints
                    height = Dimension.fillToConstraints
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    if (indicatorIndex == userBean.picList.size - 1) {
                        yRotateTag = (yRotateTag.absoluteValue + 0.1f)
                        return@clickable
                    }
                    indicatorIndex++
                }
        )

        if (userBean.picList.isNotEmpty()) {
            PicIndicator(
                modifier = Modifier.constrainAs(topIndicatorContent) {
                    top.linkTo(parent.top, 15.dp)
                    start.linkTo(parent.start, 20.dp)
                    end.linkTo(parent.end, 20.dp)
                    width = Dimension.fillToConstraints
                }, totalNum = userBean.picList.size, curIndex = indicatorIndex
            )
        }


    }

}

@Composable
@Preview
private fun PreviewTanTanSingleCard() {
    Box(
        Modifier
            .fillMaxSize()
            .padding(30.dp)) {
        TanTanSingleCard(
            Modifier
                .fillMaxSize(),
            userBean = TanTanUserBean(
                name = "等待一个人",
                picList = (0..4).map { "" },
                basicDetail = TanTanUserBean.BasicDetail(
                    isMale = false,
                    age = 14,
                    tagList = listOf(

                    )
                ),
                recentPost = TanTanUserBean.RecentPost(
                    level = 2,
                    picList = (0..4).map { "" }
                )
            )
        )
    }
}


private val picIndicatorSpacing = 5.dp

@Composable
private fun PicIndicator(
    modifier: Modifier = Modifier,
    @IntRange(from = 1) totalNum: Int,
    @IntRange(from = 0) curIndex: Int,
) {
    BoxWithConstraints(modifier) {
        val eachItemWidth = (maxWidth - picIndicatorSpacing * (totalNum - 1)) / totalNum
        val movableIndicatorOffset by animateIntAsState(
            targetValue = with(LocalDensity.current) { ((eachItemWidth + picIndicatorSpacing) * curIndex).roundToPx() },
            label = ""
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(2.dp),
            horizontalArrangement = Arrangement.spacedBy(picIndicatorSpacing)
        ) {
            repeat(totalNum) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .weight(1f, false)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White.copy(0.2f))
                )
            }
        }
        Box(
            Modifier
                .width(eachItemWidth)
                .height(2.dp)
                .offset { IntOffset(movableIndicatorOffset, 0) }
                .clip(RoundedCornerShape(50))
                .background(Color.White)
        )
    }


}