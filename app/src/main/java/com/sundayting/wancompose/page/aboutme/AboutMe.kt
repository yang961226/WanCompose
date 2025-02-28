package com.sundayting.wancompose.page.aboutme

import android.content.Intent
import android.graphics.Picture
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBarWithBackButtonContent
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.aboutme.AboutMe.AboutMePage
import com.sundayting.wancompose.page.aboutme.AboutMe.SponsorPage
import com.sundayting.wancompose.theme.AlwaysDarkModeArea
import com.sundayting.wancompose.theme.LocalWanColors
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme
import com.sundayting.wancompose.theme.darkColors

object AboutMe : WanComposeDestination {
    override val route: String
        get() = "关于作者"

    fun NavController.navigateToAboutMe() {
        navigate(route) {
            launchSingleTop = true
        }
    }

    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        navController: NavController = rememberNavController(),
        aboutMeViewModel: AboutMeViewModel = hiltViewModel(),
    ) {

        WanTheme {
            TitleBarWithContent(
                modifier = modifier,
                titleBarContent = {
                    TitleBarWithBackButtonContent(onClickBackButton = {
                        navController.popBackStack()
                    }) {
                        Text(
                            text = stringResource(id = R.string.about_me),
                            style = TitleTextStyle,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            ) {

                Box(
                    Modifier
                        .fillMaxSize()
                ) {
                    Box(Modifier.blur(radius = 100.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_my_head),
                            modifier = Modifier
                                .fillMaxSize(),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            Modifier
                                .matchParentSize()
                                .background(Color.Black.copy(0.3f))
                        )
                    }
                    val pagerState = rememberPagerState { 2 }
                    VerticalPager(
                        state = pagerState,
                        flingBehavior = PagerDefaults.flingBehavior(
                            state = pagerState,
                            snapPositionalThreshold = 0.1f
                        )
                    ) { page ->
                        when (page) {
                            0 -> AboutMePage(Modifier.fillMaxSize())

                            else -> SponsorPage(
                                Modifier.fillMaxSize(),
                                onClickAlipay = {
                                    aboutMeViewModel.saveAlipayPic(it)
                                },
                                onClickWeChat = {
                                    aboutMeViewModel.saveWeChatPic(it)
                                }
                            )
                        }
                    }
                }
            }
        }


    }

    @Composable
    fun AboutMePage(
        modifier: Modifier = Modifier,
    ) {

        val context = LocalContext.current
        AlwaysDarkModeArea {
            Column(
                modifier.padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.ic_my_head),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .size(70.dp)
                        .clip(CircleShape)
                        .border(0.5.dp, shape = CircleShape, color = Color.Gray.copy(0.2f)),
                )

                Text(
                    text = stringResource(id = R.string.my_name),
                    style = WanTheme.typography.h5.copy(
                        color = WanTheme.colors.level1TextColor
                    )
                )

                Spacer(Modifier.height(20.dp))

                Text(
                    text = stringResource(id = R.string.my_slogan),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level1TextColor
                    )
                )

                Spacer(Modifier.height(60.dp))

                InfoLine(
                    iconResId = R.drawable.ic_juejin,
                    title = stringResource(id = R.string.title_juejin),
                    data = "https://juejin.cn/user/114798491603527",
                    onClickData = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://juejin.cn/user/114798491603527")
                            )
                        )
                    }
                )

                Spacer(Modifier.height(20.dp))

                InfoLine(
                    iconResId = R.drawable.ic_blog,
                    title = stringResource(id = R.string.title_blog),
                    data = "https://yang961226.github.io/",
                    onClickData = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://yang961226.github.io/")
                            )
                        )
                    }
                )

                Spacer(
                    Modifier
                        .fillMaxSize()
                        .weight(1f, false)
                )

                Text(
                    text = stringResource(id = R.string.aboue_me_next_page_tips),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level2TextColor
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))
                Image(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .size(15.dp)
                        .rotate(-90f),
                    colorFilter = ColorFilter.tint(Color.White.copy(0.8f))
                )

            }
        }
    }

    @Composable
    fun InfoLine(
        modifier: Modifier = Modifier,
        @DrawableRes
        iconResId: Int,
        title: String,
        data: String,
        onClickData: () -> Unit = {},
    ) {

        Row(
            modifier,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                painter = painterResource(id = iconResId),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(WanTheme.colors.level1TextColor)
            )

            Spacer(Modifier.width(10.dp))

            Text(
                text = title,
                style = WanTheme.typography.h7.copy(
                    color = WanTheme.colors.level1TextColor
                )
            )

            Spacer(
                Modifier
                    .fillMaxWidth()
                    .weight(1f, false)
            )

            Text(
                text = data,
                style = WanTheme.typography.h8.copy(
                    color = WanTheme.colors.level2TextColor
                ),
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onClickData() }
            )

        }

    }

    @Composable
    fun SponsorPage(
        modifier: Modifier = Modifier,
        onClickAlipay: (Picture) -> Unit = {},
        onClickWeChat: (Picture) -> Unit = {},
    ) {

        val pictureAlipay = remember { Picture() }
        val pictureWeChat = remember { Picture() }

        Column(
            modifier
                .padding(horizontal = 100.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(50.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_alipay_receive_money),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithCache {
                        val width = this.size.width.toInt()
                        val height = this.size.height.toInt()
                        onDrawWithContent {
                            val pictureCanvas =
                                androidx.compose.ui.graphics.Canvas(
                                    pictureAlipay.beginRecording(
                                        width,
                                        height
                                    )
                                )
                            draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                this@onDrawWithContent.drawContent()
                            }
                            pictureAlipay.endRecording()

                            drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(pictureAlipay) }
                        }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onClickAlipay(pictureAlipay) },
                contentScale = ContentScale.FillWidth
            )

            AlwaysDarkModeArea {
                Text(
                    text = stringResource(id = R.string.click_to_save_pic),
                    style = WanTheme.typography.h7.copy(
                        color = WanTheme.colors.level2TextColor
                    ),
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }


            Image(
                painter = painterResource(id = R.drawable.ic_wechat_receive_money),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithCache {
                        val width = this.size.width.toInt()
                        val height = this.size.height.toInt()
                        onDrawWithContent {
                            val pictureCanvas =
                                androidx.compose.ui.graphics.Canvas(
                                    pictureWeChat.beginRecording(
                                        width,
                                        height
                                    )
                                )
                            draw(this, this.layoutDirection, pictureCanvas, this.size) {
                                this@onDrawWithContent.drawContent()
                            }
                            pictureWeChat.endRecording()

                            drawIntoCanvas { canvas -> canvas.nativeCanvas.drawPicture(pictureWeChat) }
                        }
                    }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = ripple()
                    ) { onClickWeChat(pictureWeChat) },
                contentScale = ContentScale.FillWidth
            )
        }

    }

}

@Composable
@Preview(showBackground = true)
fun PreviewAboutMePage() {
    CompositionLocalProvider(LocalWanColors provides darkColors()) {
        Box(Modifier.blur(radius = 100.dp)) {
            Image(
                painter = painterResource(id = R.drawable.ic_my_head),
                modifier = Modifier
                    .fillMaxSize(),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(0.3f))
            )
        }

        AboutMePage(
            Modifier
                .fillMaxSize()
        )
    }

}

@Composable
@Preview(showBackground = true)
fun PreviewSponsorPage() {
    SponsorPage()
}