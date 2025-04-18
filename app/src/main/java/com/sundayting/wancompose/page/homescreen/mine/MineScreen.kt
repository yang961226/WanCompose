package com.sundayting.wancompose.page.homescreen.mine

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.event.LocalEventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.helper.LocalVibratorHelper
import com.sundayting.wancompose.common.helper.VibratorHelper
import com.sundayting.wancompose.function.UserLoginFunction.UserEntity
import com.sundayting.wancompose.page.aboutme.AboutMe.navigateToAboutMe
import com.sundayting.wancompose.page.homescreen.HomeScreen
import com.sundayting.wancompose.page.homescreen.article.ui.StickColor
import com.sundayting.wancompose.page.homescreen.mine.point.PointScreen.navigateToPointScreen
import com.sundayting.wancompose.page.homescreen.mine.share.MyCollectedArticle.navigateToMyCollectedScreen
import com.sundayting.wancompose.page.myshare.MyShareScreen.navigateToMyShareScreen
import com.sundayting.wancompose.page.setting.SettingScreen.navigateToSettingScreen
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop

object MineScreen : HomeScreen.HomeScreenPage {

    override val route: String
        get() = "个人页"

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        userEntity: UserEntity?,
        navController: NavController = rememberNavController(),
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier
                .verticalScroll(scrollState)
                .background(WanTheme.colors.level2BackgroundColor)
        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(WanTheme.colors.primaryColor)
                    .padding(top = 50.dp, bottom = 30.dp)
            ) {
                val (
                    headContent,
                    nickContent,
                    levelContent,
                ) = createRefs()

                var sizeTag by remember { mutableStateOf(false) }
                val vibrationScaleAnimate = remember { Animatable(1f) }
                LaunchedEffect(Unit) {
                    snapshotFlow { sizeTag }.drop(1).collectLatest {
                        vibrationScaleAnimate.animateTo(
                            targetValue = 1f,
                            spring(dampingRatio = Spring.DampingRatioHighBouncy),
                            initialVelocity = -15f
                        )
                    }
                }

                val vibratorHelper = LocalVibratorHelper.current

                Image(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .graphicsLayer {
                            scaleX = vibrationScaleAnimate.value
                            scaleY = vibrationScaleAnimate.value
                        }
                        .constrainAs(headContent) {
                            centerHorizontallyTo(parent)
                        }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            sizeTag = sizeTag.not()
                            vibratorHelper.headVibrate()
                        },
                    painter = painterResource(id = R.drawable.ic_login_icon),
                    contentDescription = null
                )

                if (userEntity != null) {
                    Text(
                        text = userEntity.nick,
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 18.sp
                        ),
                        modifier = Modifier.constrainAs(nickContent) {
                            centerHorizontallyTo(parent)
                            top.linkTo(headContent.bottom, 15.dp)
                        }
                    )

                    Row(
                        Modifier.constrainAs(levelContent) {
                            centerHorizontallyTo(parent)
                            top.linkTo(nickContent.bottom, 5.dp)
                        }
                    ) {
                        Text(
                            text = stringResource(id = R.string.level_d, userEntity.level),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White.copy(0.8f)
                            )
                        )

                        Spacer(Modifier.width(5.dp))

                        Text(
                            text = stringResource(
                                id = R.string.rank_s,
                                userEntity.rank.takeIf { it > 0 }?.toString() ?: "9999+"
                            ),
                            style = TextStyle(
                                fontSize = 14.sp,
                                color = Color.White.copy(0.8f)
                            )
                        )
                    }
                } else {
                    val eventManager = LocalEventManager.current
                    Button(
                        onClick = {
                            eventManager.emitEvent(ShowLoginPageEvent)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WanTheme.colors.tipColor,
                        ),
                        shape = RoundedCornerShape(50),
                        contentPadding = PaddingValues(horizontal = 50.dp, vertical = 10.dp),
                        modifier = Modifier.constrainAs(createRef()) {
                            top.linkTo(headContent.bottom, 10.dp)
                            centerHorizontallyTo(headContent)
                        }
                    ) {
                        Text(stringResource(R.string.to_login))
                    }
                }
            }
            if (userEntity != null) {
                MineScreenSingleLine(
                    title = stringResource(id = R.string.my_points),
                    resId = R.drawable.ic_point,
                    endContent = {
                        Text(
                            modifier = Modifier.padding(end = 10.dp),
                            text = (userEntity.coinCount).toString(),
                            style = WanTheme.typography.h7.copy(
                                color = WanTheme.colors.level3TextColor
                            )
                        )
                    },
                    onClick = {
                        navController.navigateToPointScreen()
                    }
                )
                MineScreenSingleLine(
                    title = stringResource(id = R.string.my_share),
                    resId = R.drawable.ic_share,
                    onClick = {
                        navController.navigateToMyShareScreen()
                    }
                )
                MineScreenSingleLine(
                    title = stringResource(id = R.string.my_collect),
                    resId = R.drawable.ic_like,
                    onClick = {
                        navController.navigateToMyCollectedScreen()
                    }
                )
            }

            MineScreenSingleLine(
                title = stringResource(id = R.string.about_me),
                resId = R.drawable.ic_about,
                endContent = {
                    Text(
                        modifier = Modifier.padding(end = 10.dp),
                        text = stringResource(id = R.string.buy_he_coffee),
                        style = WanTheme.typography.h8.copy(
                            color = StickColor
                        )
                    )
                },
                onClick = {
                    navController.navigateToAboutMe()
                }
            )
            MineScreenSingleLine(
                title = stringResource(id = R.string.system_setting),
                resId = R.drawable.ic_setting,
                onClick = {
                    navController.navigateToSettingScreen()
                }
            )
        }
    }

}

@Composable
private fun MineScreenSingleLine(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes resId: Int,
    endContent: @Composable (RowScope.() -> Unit)? = null,
    onClick: () -> Unit = {},
) {

    Row(
        modifier
            .clickable(
                interactionSource = null,
                indication = ripple()
            ) { onClick() }
            .padding(15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .size(20.dp),
            colorFilter = ColorFilter.tint(WanTheme.colors.primaryColor)
        )
        Text(
            text = title,
            style = WanTheme.typography.h7.copy(
                color = WanTheme.colors.level1TextColor
            ),
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .weight(1f, false)
        )
        if (endContent != null) {
            endContent()
        }
        Image(
            painter = painterResource(id = R.drawable.ic_arrow_right),
            contentDescription = null,
            modifier = Modifier.size(15.dp),
            colorFilter = ColorFilter.tint(Color.Gray.copy(0.8f))
        )
    }

}

@Composable
@Preview
private fun PreviewMineScreen() {
    val context = LocalContext.current
    CompositionLocalProvider(
        LocalVibratorHelper provides remember {
            VibratorHelper(context)
        }
    ) {
        MineScreen.Screen(
            modifier = Modifier.fillMaxSize(),
            remember {
                UserEntity(
                    id = 1104,
                    nick = "我是名字",
                    coinCount = 0,
                    level = 0,
                    rank = -1
                )
            }
        )
    }

}

@Composable
@Preview
private fun PreviewMineScreen2() {
    val context = LocalContext.current
    CompositionLocalProvider(
        LocalVibratorHelper provides remember {
            VibratorHelper(context)
        }
    ) {
        MineScreen.Screen(
            modifier = Modifier.fillMaxSize(),
            null
        )
    }

}