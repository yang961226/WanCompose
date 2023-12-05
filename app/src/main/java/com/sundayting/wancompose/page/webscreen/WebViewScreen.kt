package com.sundayting.wancompose.page.webscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.platform.LocalContext
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
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.WebViewState
import com.google.accompanist.web.rememberWebViewNavigator
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBar
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.theme.WanColors
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


    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        webViewState: WebViewState,
        navController: NavController,
    ) {

        var title by remember { mutableStateOf("") }

        val context = LocalContext.current

        TitleBarWithContent(
            modifier = modifier.navigationBarsPadding(),
            titleBarContent = {
                WebTitle(title, navController)
            },
        ) {
            val client = remember(context) {
                object : AccompanistWebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView,
                        request: WebResourceRequest,
                    ): Boolean {
                        var uri = request.url
                        val scheme = uri.scheme
                        if (scheme == "http") {
                            uri = Uri.parse("https://" + uri.host + uri.path)
                        }
                        if (uri.scheme != "https" && uri.scheme != "http") {
                            context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                        } else {
                            view.loadUrl(uri.toString())
                        }
                        return true
                    }


                    override fun onPageFinished(view: WebView?, url: String?) {
                        title = view?.title.orEmpty()
                    }
                }
            }
            val navigator: WebViewNavigator = rememberWebViewNavigator()
            ConstraintLayout(
                Modifier
                    .fillMaxSize()
            ) {
                val (
                    webViewContent,
                    webToolContent,
                ) = createRefs()
                WebView(
                    navigator = navigator,
                    modifier = Modifier
                        .fillMaxSize()
                        .constrainAs(webViewContent) {
                            centerTo(parent)
                        },
                    factory = {
                        WebView(it).also { webView ->
                            webView.settings.apply {
                                javaScriptEnabled = true
                                javaScriptCanOpenWindowsAutomatically = false
                                allowFileAccess = true
                                domStorageEnabled = true
                                setGeolocationEnabled(true)
                                cacheMode = WebSettings.LOAD_DEFAULT
                                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                CookieManager.getInstance()
                                    .setAcceptThirdPartyCookies(webView, true);
                            }
                        }
                    },
                    state = webViewState,
                    client = client
                )

                val context = LocalContext.current


                WebToolWidget(
                    Modifier.constrainAs(webToolContent) {
                        start.linkTo(parent.start, 30.dp)
                        bottom.linkTo(parent.bottom, 60.dp)
                    },
                    loadingProgress = remember {
                        derivedStateOf { (webViewState.loadingState as? LoadingState.Loading)?.progress }
                    }.value,
                    onClickBack = {
                        if (navigator.canGoBack) {
                            navigator.navigateBack()
                        } else {
                            navController.popBackStack()
                        }
                    },
                    onClickBookmark = {

                    },
                    onClickBrowser = {
                        context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(webViewState.lastLoadedUrl)
                            )
                        )
                    },
                    onClickLike = {

                    },
                    onClickShare = {

                    },
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
            .size(25.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(radius = 25.dp, bounded = false),
            ) { navController.popBackStack() },
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
    loadingProgress: Float? = null,
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


    Box(modifier.size(60.dp), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = loadingProgress != null && loadingProgress != 1f
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = WanColors.TopColor,
                progress = loadingProgress ?: 0f,
            )
        }
        WebToolButton(
            Modifier
                .offset(y = buttonOneOffset)
                .alpha(alpha)
                .clickable(
                    enabled = open,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        radius = 25.dp
                    )
                ) { onClickLike() },
            resId = R.drawable.ic_like
        )
        WebToolButton(
            Modifier
                .offset(y = buttonTwoOffset)
                .alpha(alpha)
                .clickable(
                    enabled = open,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        radius = 25.dp
                    )
                ) { onClickBookmark() },
            resId = R.drawable.ic_bookmark
        )
        WebToolButton(
            Modifier
                .offset(y = buttonThreeOffset)
                .alpha(alpha)
                .clickable(
                    enabled = open,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        radius = 25.dp
                    )
                ) { onClickBrowser() },
            resId = R.drawable.ic_net
        )
        WebToolButton(
            Modifier
                .offset(y = buttonFourOffset)
                .alpha(alpha)
                .clickable(
                    enabled = open,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(
                        radius = 25.dp
                    )
                ) { onClickShare() },
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
                    indication = rememberRipple(
                        radius = 25.dp
                    ),
                    onLongClick = {
                        open = open.not()
                    },
                    onClick = {
                        if (open) {
                            open = false
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

