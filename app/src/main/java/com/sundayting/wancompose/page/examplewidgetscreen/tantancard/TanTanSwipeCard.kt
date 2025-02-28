package com.sundayting.wancompose.page.examplewidgetscreen.tantancard

import androidx.annotation.DrawableRes
import androidx.annotation.IntRange
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.lerp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.helper.LocalVibratorHelper
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt


/**
 * 探探用户对象实体类
 */
data class TanTanUserBean(
    val uid: Int,
    val name: String,
    val picList: List<String>,
    val basicDetail: BasicDetail,
    val recentPost: RecentPost,
) {

    data class BasicDetail(
        val isMale: Boolean = false,
        val age: Int? = null,
        val tagList: List<Tag> = emptyList(),
        val location: String,
    ) {

        data class Tag(
            @DrawableRes
            val icon: Int? = null,
            val content: String,
        )

    }

    data class RecentPost(
        val picList: List<String> = emptyList(),
    )

}

@Composable
@Preview
private fun PreviewTanTanSwipeCard() {
    val list = remember {
        mutableStateListOf<TanTanUserBean>().apply {
            addAll(TestExample.userList)
        }
    }
    TanTanSwipeCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 40.dp, horizontal = 20.dp),
        userList = list.asReversed(),
        onSwipeToDismiss = {
            list.removeFirst()
            list.add(TestExample.getNextUser())
        }
    )
}


/**
 * 探探滑卡
 */
@Composable
fun TanTanSwipeCard(
    modifier: Modifier = Modifier,
    userList: List<TanTanUserBean>,
    onSwipeToDismiss: () -> Unit = {},
) {

    val rememberList by remember(userList) {
        derivedStateOf { userList }
    }

    val scope = rememberCoroutineScope()
    val offsetAnimate = remember { Animatable(IntOffset.Zero, IntOffset.VectorConverter) }
    var dragTopHalf by remember { mutableStateOf(false) }

    val scrollThreshold = with(LocalDensity.current) { 200.dp.toPx() }
    val scrollPercentage by remember(scrollThreshold) {
        derivedStateOf {
            (offsetAnimate.value.x.toFloat() / scrollThreshold).coerceIn(-1f, 1f)
        }
    }

    Box(modifier, contentAlignment = Alignment.Center) {
        rememberList.fastForEachIndexed { index, bean ->
            key(bean.uid) {
                val rememberIndex by rememberUpdatedState(index)
                val indexFromTop by remember {
                    derivedStateOf { rememberList.size - 1 - rememberIndex }
                }

                BoxWithConstraints(
                    Modifier.fillMaxSize()
                ) {
                    TanTanSingleCard(
                        userBean = bean,
                        topIndexProvider = { indexFromTop },
                        modifier = Modifier
                            .fillMaxSize()
                            .composed {
                                if (indexFromTop == 0) {
                                    val targetRotationZ by remember {
                                        derivedStateOf {
                                            lerp(
                                                0f,
                                                5f,
                                                scrollPercentage.absoluteValue
                                            ) * (if (scrollPercentage >= 0) 1f else -1f) * (if (dragTopHalf) 1f else -1f)
                                        }
                                    }
                                    Modifier
                                        .offset { offsetAnimate.value }
                                        .rotate(targetRotationZ)
                                        .pointerInput(Unit) {
                                            fun swipeToDismiss() {
                                                scope.launch {
                                                    offsetAnimate.animateTo(
                                                        offsetAnimate.value + IntOffset(
                                                            (1000 * if (offsetAnimate.value.x > 0f) 1f else -1f).roundToInt(),
                                                            0
                                                        ),
                                                        animationSpec = tween(200)
                                                    )
                                                    onSwipeToDismiss()
                                                    offsetAnimate.snapTo(IntOffset.Zero)
                                                }
                                            }

                                            fun toInitLoc() {
                                                scope.launch {
                                                    offsetAnimate.animateTo(IntOffset.Zero)
                                                }
                                            }

                                            fun onDragEndOrCancel() {
                                                if (offsetAnimate.value.x.absoluteValue > 50.dp.toPx()) {
                                                    swipeToDismiss()
                                                } else {
                                                    toInitLoc()
                                                }
                                            }
                                            detectDragGestures(
                                                onDragStart = {
                                                    dragTopHalf = it.y < size.height / 2
                                                },
                                                onDrag = { _, dragAmount ->
                                                    scope.launch {
                                                        offsetAnimate.snapTo(
                                                            offsetAnimate.value + IntOffset(
                                                                dragAmount.x.roundToInt(),
                                                                dragAmount.y.roundToInt()
                                                            )
                                                        )
                                                    }
                                                },
                                                onDragCancel = {
                                                    onDragEndOrCancel()
                                                },
                                                onDragEnd = {
                                                    onDragEndOrCancel()
                                                }
                                            )
                                        }
                                } else {
                                    Modifier.graphicsLayer {
                                        val scale =
                                            (1f - (indexFromTop - scrollPercentage.absoluteValue) * 0.08f).coerceAtLeast(
                                                0.8f
                                            )
                                        scaleX = scale
                                        scaleY = scale

                                        translationY =
                                            -((maxHeight.toPx() * (1 - scale) / 2) + ((indexFromTop - scrollPercentage.absoluteValue).coerceAtMost(
                                                2f
                                            )) * 5.dp.toPx())
                                    }
                                }
                            }

                    )
                    DislikeOrLikeButtons(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 20.dp)
                            .padding(horizontal = 20.dp),
                        scrollProgressProvider = { scrollPercentage }
                    )
                }
            }
        }
    }
}

@Composable
private fun TanTanSingleCard(
    modifier: Modifier = Modifier,
    userBean: TanTanUserBean,
    topIndexProvider: () -> Int,
) {

    var yRotateTag by remember { mutableFloatStateOf(0f) }

    val yRotateAnimate = remember {
        Animatable(0f)
    }


    if (LocalInspectionMode.current.not()) {
        val vibratorHelper = LocalVibratorHelper.current
        LaunchedEffect(Unit) {
            snapshotFlow { yRotateTag }.drop(1).collectLatest {
                vibratorHelper.vibrateClick()
                yRotateAnimate.animateTo(
                    0f, animationSpec = spring(
                        stiffness = Spring.StiffnessMediumLow,
                    ), initialVelocity = if (it > 0) 100f else -100f
                )
            }
        }
    }

    ConstraintLayout(
        modifier
            .graphicsLayer {
                rotationY = yRotateAnimate.value
            }
            .border(
                0.5.dp,
                color = Color.Gray.copy(0.5f),
                shape = RoundedCornerShape(30.dp)
            )
            .clip(RoundedCornerShape(30.dp))
    ) {
        val (
            picContent,
            topIndicatorContent,
            bottomMaskContent,
            detailContent,
        ) = createRefs()

        var indicatorIndex by remember(userBean.picList.size) { mutableIntStateOf(0) }

        AsyncImage(
            modifier = Modifier
                .constrainAs(picContent) {
                    height = Dimension.fillToConstraints
                    width = Dimension.fillToConstraints
                    centerTo(parent)
                }
                .background(Color.Gray),
            model = ImageRequest
                .Builder(LocalContext.current)
                .data(userBean.picList.getOrNull(indicatorIndex))
                .apply {
                    if (LocalInspectionMode.current) {
                        placeholder(R.drawable.default_head_pic)
                    }
                }
                .error(R.drawable.default_head_pic)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        val isTopCard by remember {
            derivedStateOf { topIndexProvider() == 0 }
        }

        HalvedClickArea(
            enable = isTopCard,
            modifier = Modifier.constrainAs(
                createRef()
            ) {
                top.linkTo(picContent.top)
                bottom.linkTo(picContent.bottom)
                start.linkTo(picContent.start)
                end.linkTo(picContent.end)
            }, onClickStart = {
                if (indicatorIndex == 0) {
                    yRotateTag = -(yRotateTag.absoluteValue + 0.1f)
                } else {
                    indicatorIndex--
                }
            }, onClickEnd = {
                if (indicatorIndex == userBean.picList.size - 1) {
                    yRotateTag = (yRotateTag.absoluteValue + 0.1f)
                } else {
                    indicatorIndex++
                }
            }
        )


        AnimatedVisibility(
            visible = isTopCard && userBean.picList.size > 1,
            label = "",
            modifier = Modifier.constrainAs(topIndicatorContent) {
                top.linkTo(parent.top, 15.dp)
                start.linkTo(parent.start, 20.dp)
                end.linkTo(parent.end, 20.dp)
                width = Dimension.fillToConstraints
            },
            enter = fadeIn(),
            exit = ExitTransition.None
        ) {
            PicIndicator(
                modifier = Modifier, totalNum = userBean.picList.size, curIndex = indicatorIndex
            )
        }

        BottomMask(Modifier.constrainAs(bottomMaskContent) {
            centerHorizontallyTo(parent)
            width = Dimension.fillToConstraints
            height = Dimension.wrapContent
            bottom.linkTo(parent.bottom)
        })

        Column(
            Modifier
                .padding(horizontal = 20.dp)
                .constrainAs(detailContent) {
                    bottom.linkTo(parent.bottom, 100.dp)
                    centerHorizontallyTo(parent, 0f)
                }
        ) {
            Text(
                text = userBean.name,
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold
                )
            )

            AnimatedContent(
                targetState = ((indicatorIndex > 0) to userBean),
                label = "",
                transitionSpec = {
                    if (initialState.first == targetState.first) {
                        fadeIn(snap()) togetherWith fadeOut(snap())
                    }
                    //如果目标是第一张图片的情况，则左滑进场，瞬时消失离场
                    else if (targetState.first.not()) {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith fadeOut(snap())
                    }
                    //如果目标是第一张图片的情况，则右滑进场，瞬时消失离场
                    else {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith fadeOut(snap())
                    }.using(
                        SizeTransform(clip = false, sizeAnimationSpec = { _, _ -> snap() })
                    )
                }
            ) { paramsPair ->
                val user = paramsPair.second
                if (paramsPair.first.not()) {
                    Column {
                        Text(
                            text = userBean.basicDetail.location,
                            style = TextStyle(
                                fontSize = 15.sp,
                                color = Color.White,
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            if (user.basicDetail.isMale) {
                                TagContent(
                                    title = user.basicDetail.age?.toString().orEmpty(),
                                    iconRes = if (user.basicDetail.isMale) R.drawable.ic_male else R.drawable.ic_female,
                                    backgroundColor = if (user.basicDetail.isMale) Color(0xFF4396f8) else Color(
                                        0xFFEB5992
                                    )
                                )
                            }
                            user.basicDetail.tagList.forEach {
                                TagContent(title = it.content, iconRes = it.icon)
                            }
                        }
                    }

                } else {
                    Column {
                        Text(
                            text = "近期动态", style = TextStyle(
                                fontSize = 15.sp,
                                color = Color.White,
                            )
                        )
                        Spacer(Modifier.height(10.dp))
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            userBean.recentPost.picList.forEach {
                                RecentPostSingleContent(
                                    modifier = Modifier.size(40.dp),
                                    imgUrl = it
                                )
                            }
                        }
                    }
                }
            }
        }


    }

}

@Composable
private fun TagContent(
    modifier: Modifier = Modifier,
    @DrawableRes
    iconRes: Int? = null,
    title: String,
    backgroundColor: Color? = null,
) {
    Row(
        modifier
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (backgroundColor != null) {
                    Modifier.background(backgroundColor)
                } else {
                    Modifier.background(Color.Gray.copy(0.4f))
                }
            )
            .padding(vertical = 5.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconRes != null) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }
        Text(
            text = title,
            style = TextStyle(
                fontSize = 15.sp,
                color = Color.White
            )
        )
    }
}

@Composable
private fun RecentPostSingleContent(
    modifier: Modifier = Modifier,
    imgUrl: String,
) {
    AsyncImage(
        modifier = modifier
            .aspectRatio(1f)
            .border(1.dp, color = Color.White.copy(0.3f), shape = RoundedCornerShape(4.dp))
            .padding(1.dp)
            .clip(RoundedCornerShape(4.dp)),
        model = ImageRequest
            .Builder(LocalContext.current)
            .data(imgUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun BottomMask(modifier: Modifier) {
    Column(modifier) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(130.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(0.8f)
                        )
                    )
                )
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(30.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(0.8f),
                            Color.Black
                        )
                    )
                )
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(Color.Black)
        )
    }
}

@Composable
private fun HalvedClickArea(
    modifier: Modifier = Modifier,
    onClickStart: () -> Unit,
    onClickEnd: () -> Unit,
    enable: Boolean,
) {

    Row(modifier) {
        Box(
            Modifier
                .fillMaxSize()
                .weight(1f, false)
                .clickable(
                    enabled = enable,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClickStart()
                }
        )
        Box(
            Modifier
                .fillMaxSize()
                .weight(1f, false)
                .clickable(
                    enabled = enable,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    onClickEnd()
                }
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
                        .background(Color.White.copy(0.4f))
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

private val startColor = Color.Gray.copy(0.5f)
private val closeEndColor = Color(0xFFF2BF42)
private val likeEndColor = Color(0xFFD85140)

@Composable
private fun DislikeOrLikeButtons(
    modifier: Modifier = Modifier,
    scrollProgressProvider: () -> Float,
) {

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        val closeScale by remember {
            derivedStateOf {
                lerp(1f, 0.9f, scrollProgressProvider().coerceAtMost(0f).absoluteValue)
            }
        }
        val closeColor by remember {
            derivedStateOf {
                lerp(
                    startColor,
                    closeEndColor,
                    scrollProgressProvider().coerceAtMost(0f).absoluteValue
                )
            }
        }
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .height(45.dp)
                .graphicsLayer {
                    scaleX = closeScale
                    scaleY = closeScale
                }
                .clip(RoundedCornerShape(50))
                .drawBehind {
                    drawRect(closeColor)
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close_2),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(Color.White)
            )
        }

        Spacer(Modifier.width(20.dp))

        val likeScale by remember {
            derivedStateOf {
                lerp(1f, 0.9f, scrollProgressProvider().coerceAtLeast(0f))
            }
        }
        val likeColor by remember {
            derivedStateOf {
                lerp(
                    startColor,
                    likeEndColor,
                    scrollProgressProvider().coerceAtLeast(0f)
                )
            }
        }

        val likeIconColor by remember {
            derivedStateOf {
                lerp(
                    likeEndColor,
                    Color.White,
                    scrollProgressProvider().coerceAtLeast(0f)
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f, false)
                .height(45.dp)
                .graphicsLayer {
                    scaleX = likeScale
                    scaleY = likeScale
                }
                .clip(RoundedCornerShape(50))
                .drawBehind {
                    drawRect(likeColor)
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_like2),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(likeIconColor)
            )
        }

    }


}

@Composable
private fun TopDislikeOrLikeButtons(
    modifier: Modifier = Modifier,
    scrollProgressProvider: () -> Float,
) {
    Box(modifier) {
        Box(
            Modifier
                .size(70.dp)
                .graphicsLayer {
                    alpha = lerp(0f, 1f, scrollProgressProvider().coerceAtLeast(0f))
                    lerp(0.5f, 1f, scrollProgressProvider().coerceAtLeast(0f)).let {
                        scaleX = it
                        scaleY = it
                    }
                }
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopStart),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_like2),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(likeEndColor)
            )
        }
        Box(
            Modifier
                .size(70.dp)
                .graphicsLayer {
                    alpha = lerp(0f, 1f, scrollProgressProvider().coerceAtMost(0f).absoluteValue)
                    lerp(0.5f, 1f, scrollProgressProvider().coerceAtMost(0f).absoluteValue).let {
                        scaleX = it
                        scaleY = it
                    }
                }
                .clip(CircleShape)
                .background(Color.White)
                .align(Alignment.TopEnd),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close_2),
                contentDescription = null,
                modifier = Modifier.size(30.dp),
                colorFilter = ColorFilter.tint(closeEndColor)
            )
        }
    }

}