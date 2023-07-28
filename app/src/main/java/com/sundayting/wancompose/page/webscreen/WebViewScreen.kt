package com.sundayting.wancompose.page.webscreen

import android.net.Uri
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewState
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBar
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object WebViewScreen : WanComposeDestination {
    override val route: String
        get() = "浏览器"

    const val urlArg = "urlKey"

    val routeWithArgs = "$route/{$urlArg}"

    val arguments = listOf(
        navArgument(urlArg) { type = NavType.StringType }
    )

    fun NavController.navigateToWebViewScreen(url: String) {
        navigate(
            "$route/${
                URLEncoder.encode(
                    url,
                    StandardCharsets.UTF_8.toString()
                )
            }"
        ) {
            launchSingleTop = true
        }
    }


    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        webViewState: WebViewState,
        navController: NavController,
    ) {

        var title by remember { mutableStateOf("") }

        TitleBarWithContent(
            modifier = modifier.navigationBarsPadding(),
            titleBarContent = {
                WebTitle(title, navController)
            },
        ) {
            ConstraintLayout(
                Modifier
                    .fillMaxSize()
            ) {
                val (
                    webViewContent,
                    webToolContent,
                ) = createRefs()
                WebView(
                    modifier = Modifier
                        .fillMaxSize()
                        .constrainAs(webViewContent) {
                            centerTo(parent)
                        },
                    state = webViewState,
                    client = object : AccompanistWebViewClient() {
                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest,
                        ): Boolean {
                            var uri = request.url
                            val scheme = uri.scheme
                            if (scheme == "http") {
                                uri = Uri.parse("https://" + uri.host + uri.path)
                            }
                            view.loadUrl(uri.toString())
                            return true
                        }

                        override fun onPageFinished(view: WebView?, url: String?) {
                            title = view?.title.orEmpty()
                        }
                    }
                )

                WebToolWidget(
                    Modifier.constrainAs(webToolContent) {
                        start.linkTo(parent.start, 30.dp)
                        bottom.linkTo(parent.bottom, 60.dp)
                    },
                    onClickBookmark = {

                    },
                    onClickBrowser = {

                    },
                    onClickLike = {

                    },
                    onClickShare = {

                    }
                )

            }
        }

    }
}

@Composable
private fun BoxScope.WebTitle(title: String, navController: NavController) {
    Text(
        modifier = Modifier
            .align(Alignment.Center)
            .padding(horizontal = 60.dp),
        text = title,
        style = TextStyle(
            fontSize = 20.sp,
            color = Color.White
        ),
        overflow = TextOverflow.Ellipsis
    )
    Image(
        painter = painterResource(id = R.drawable.ic_close),
        contentDescription = null,
        modifier = Modifier
            .align(Alignment.CenterStart)
            .padding(start = 20.dp)
            .size(20.dp)
            .clickable { navController.popBackStack() },
        colorFilter = ColorFilter.tint(Color.White)
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewWebToolWidget() {
    Box(
        Modifier
            .height(300.dp)
            .width(50.dp)
            .padding(bottom = 10.dp), contentAlignment = Alignment.BottomCenter
    ) {
        WebToolWidget()
    }

}

@Composable
private fun WebToolWidget(
    modifier: Modifier = Modifier,
    onClickBack: () -> Unit = {},
    onClickLike: () -> Unit = {},
    onClickBookmark: () -> Unit = {},
    onClickBrowser: () -> Unit = {},
    onClickShare: () -> Unit = {},
) {

    var open by remember { mutableStateOf(false) }

    val openTransition = updateTransition(targetState = open, label = "")
    val alpha by openTransition.animateFloat(label = "alpha") { isOpen ->
        if (isOpen) 1f else 0f
    }

    val rotate by openTransition.animateFloat(label = "") { isOpen ->
        if (isOpen) 180f else 0f
    }
    val buttonOneOffset by openTransition.animateDp(label = "组件1") { isOpen ->
        if (isOpen) -(buttonSize + 10.dp) else 0.dp
    }

    val buttonTwoOffset by openTransition.animateDp(label = "组件2") { isOpen ->
        if (isOpen) -((buttonSize + 10.dp) * 2) else 0.dp
    }

    val buttonThreeOffset by openTransition.animateDp(label = "组件3") { isOpen ->
        if (isOpen) -((buttonSize + 10.dp) * 3) else 0.dp
    }

    val buttonFourOffset by openTransition.animateDp(label = "组件4") { isOpen ->
        if (isOpen) -((buttonSize + 10.dp) * 4) else 0.dp
    }


    Box(modifier) {
        WebToolButton(
            Modifier
                .offset(y = buttonOneOffset)
                .alpha(alpha)
                .clickable(enabled = open) { onClickLike() },
            resId = R.drawable.ic_like
        )
        WebToolButton(
            Modifier
                .offset(y = buttonTwoOffset)
                .alpha(alpha)
                .clickable(enabled = open) { onClickBookmark() },
            resId = R.drawable.ic_bookmark
        )
        WebToolButton(
            Modifier
                .offset(y = buttonThreeOffset)
                .alpha(alpha)
                .clickable(enabled = open) { onClickBrowser() },
            resId = R.drawable.ic_net
        )
        WebToolButton(
            Modifier
                .offset(y = buttonFourOffset)
                .alpha(alpha)
                .clickable(enabled = open) { onClickShare() },
            resId = R.drawable.ic_share
        )
        WebToolButton(
            modifier = Modifier
                .graphicsLayer {
                    this.rotationZ = rotate
                }
                .combinedClickable(
                    enabled = openTransition.isRunning.not(),
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onLongClick = {
                        open = open.not()
                    },
                    onClick = {
                        if (open) {
                            open = open.not()
                        } else {
                            onClickBack()
                        }
                    }
                ),
            resId = if (open) R.drawable.ic_close else R.drawable.ic_direction_left
        )

    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewWebToolButton() {
    WebToolButton(resId = R.drawable.ic_close)
}

private val buttonSize = 50.dp

@Composable
private fun WebToolButton(
    modifier: Modifier = Modifier,
    @DrawableRes resId: Int,
) {
    Surface(
        modifier.size(buttonSize),
        shape = CircleShape,
        elevation = 2.dp,
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun WebTitle(
    modifier: Modifier = Modifier,
    title: String,
    onClickClose: () -> Unit = {},
) {
    TitleBar(modifier) {
        Text(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 60.dp),
            text = title,
            style = TextStyle(
                fontSize = 20.sp,
                color = Color.White
            ),
            overflow = TextOverflow.Ellipsis
        )
        Image(
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 20.dp)
                .size(20.dp)
                .clickable { onClickClose() },
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

