package com.sundayting.wancompose.page.webscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.helper.LocalDarkMode
import com.sundayting.wancompose.common.helper.LocalVibratorHelper
import com.sundayting.wancompose.common.ui.dialog.ShareArticleDialog
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.theme.CollectColor
import com.sundayting.wancompose.theme.TitleTextStyle
import com.sundayting.wancompose.theme.WanTheme
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


object WebViewScreen : WanComposeDestination {
    override val route: String
        get() = "浏览器"

    const val ARGS_KEY = "argumentKey"

    val routeWithArgs = "$route/{$ARGS_KEY}"

    val arguments = listOf(
        navArgument(ARGS_KEY) { type = NavType.StringType },
    )


    fun NavController.navigateToWebViewScreen(
        articleUiBean: ArticleList.ArticleUiBean,
    ) {
        navigate(
            "$route/${
                URLEncoder.encode(
                    Json.encodeToString(
                        articleUiBean,
                    ),
                    StandardCharsets.UTF_8.toString()
                )
            }"
        ) {
            launchSingleTop = true
        }
    }

    fun NavController.navigateToWebViewScreen(
        bannerUiBean: ArticleList.BannerUiBean,
    ) {
        navigateToWebViewScreen(
            ArticleList.ArticleUiBean(
                title = "",
                date = "",
                id = -1,
                isStick = false,
                chapter = ArticleList.ArticleUiBean.Chapter("", ""),
                authorOrSharedUser = ArticleList.ArticleUiBean.AuthorOrSharedUser(),
                link = bannerUiBean.linkUrl,
                desc = ""
            )
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun Screen(
        modifier: Modifier = Modifier,
        viewModel: WebViewViewModel = hiltViewModel(),
        navController: NavController,
    ) {

        var title by remember { mutableStateOf("") }

        val context = LocalContext.current

        var isOpenToolBoxOpen by rememberSaveable {
            mutableStateOf(false)
        }

        var toolPosition by remember {
            mutableStateOf(Offset.Zero)
        }

        ConstraintLayout(
            modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            val (
                webViewContent,
                webToolContent,
            ) = createRefs()

            var cachedWebView by remember { mutableStateOf<WebView?>(null) }
            val isDarkMode = LocalDarkMode.current

            cachedWebView?.let { webView ->
                BackHandler {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        navController.popBackStack()
                    }
                }

                LaunchedEffect(webView, viewModel.webViewUiState.targetUrl) {
                    webView.loadUrl(viewModel.webViewUiState.targetUrl)
                }

                LaunchedEffect(isDarkMode, webView) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.ALGORITHMIC_DARKENING)) {
                            WebSettingsCompat.setAlgorithmicDarkeningAllowed(
                                webView.settings,
                                isDarkMode
                            )
                        }
                    } else {
                        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                            WebSettingsCompat.setForceDark(
                                webView.settings,
                                if (isDarkMode) WebSettingsCompat.FORCE_DARK_ON else WebSettingsCompat.FORCE_DARK_OFF
                            )
                        }
                    }
                }
            }

            var webProgress by remember { mutableIntStateOf(0) }

            AndroidView(
                {
                    WebView(it).apply {
                        settings.javaScriptEnabled = true
                        settings.javaScriptCanOpenWindowsAutomatically = true
                        settings.allowFileAccess = true
                        settings.domStorageEnabled = true
                        settings.setGeolocationEnabled(true)
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        CookieManager.getInstance()
                            .setAcceptThirdPartyCookies(this, true)
                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest,
                            ): Boolean {
                                var uri = request.url
                                val scheme = uri.scheme
                                if (scheme == "http") {
                                    uri = ("https://" + uri.host + uri.path).toUri()
                                }
                                if (uri.scheme != "https" && uri.scheme != "http") {
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    if (intent.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(intent)
                                    }
                                    return true
                                } else {
                                    view.loadUrl(uri.toString())
                                    return false
                                }
                            }


                            override fun onPageFinished(view: WebView?, url: String?) {
                                title = view?.title.orEmpty()
                            }

                        }
                        webChromeClient = object : WebChromeClient() {
                            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                webProgress = newProgress
                            }
                        }

                        cachedWebView = this
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            while (true) {
                                val event = awaitPointerEvent()
                                if (event.type == PointerEventType.Move) {
                                    isOpenToolBoxOpen = false
                                }
                            }
                        }
                    }
                    .constrainAs(webViewContent) {
                        centerTo(parent)
                    }
            )

            val vibratorHelper = LocalVibratorHelper.current

            var showShareDialog by remember {
                mutableStateOf(false)
            }

            if (showShareDialog) {
                ShareArticleDialog(
                    title = viewModel.webViewUiState.articleUiBean.title,
                    qrString = "哈哈哈哈厕所",
                    onDismissRequest = {
                        showShareDialog = false
                    }
                )
            }

            WebToolWidget(
                Modifier
                    .constrainAs(webToolContent) {
                        start.linkTo(parent.start, 30.dp)
                        bottom.linkTo(parent.bottom, 60.dp)
                    },
                loadingProgressProvider = { webProgress },
                toolList = viewModel.webViewUiState.toolList,
                isCollect = viewModel.webViewUiState.articleUiBean.isCollect,
                openProvide = { isOpenToolBoxOpen },
                onOpenChanged = {
                    if (it) {
                        vibratorHelper.vibrateClick()
                    }
                    isOpenToolBoxOpen = it
                },
                onClick = {
                    when (it) {
                        WebToolWidgetEnum.Share -> {
                            showShareDialog = true
                        }

                        WebToolWidgetEnum.Browser -> context.startActivity(
                            Intent(
                                Intent.ACTION_VIEW,
                                viewModel.webViewUiState.targetUrl.toUri()
                            )
                        )

                        WebToolWidgetEnum.Collect -> viewModel.collectOrUnCollectArticle()

                        WebToolWidgetEnum.Back -> if (cachedWebView?.canGoBack() == true) {
                            cachedWebView?.goBack()
                        } else {
                            navController.popBackStack()
                        }
                    }
                },
                onCloseButtonPositionChanged = {
                    toolPosition = it
                }
            )

        }


        Crossfade(targetState = viewModel.webViewUiState.needShowGuide, label = "")
        { isVisible ->
            if (isVisible) {
                Box(Modifier.fillMaxSize()) {
                    Canvas(
                        Modifier
                            .fillMaxSize()
                            .pointerInput(Unit) {}
                    ) {
                        clipPath(
                            Path().apply {
                                addRoundRect(
                                    RoundRect(
                                        rect = Rect(
                                            offset = toolPosition - Offset(
                                                10.dp.toPx(),
                                                10.dp.toPx()
                                            ),
                                            size = Size(
                                                (buttonSize + 20.dp).toPx(),
                                                (buttonSize + 20.dp).toPx()
                                            )
                                        ),
                                        cornerRadius = CornerRadius(
                                            (buttonSize / 2 + 20.dp).toPx()
                                        )
                                    )
                                )
                            }, clipOp = ClipOp.Difference
                        ) {
                            drawRect(SolidColor(Color.Black.copy(alpha = 0.7f)))
                        }
                    }

                    Text(
                        text = stringResource(id = R.string.i_know),
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 70.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple()
                            ) {
                                viewModel.webViewUiState.needShowGuide = false
                            }
                            .background(WanTheme.colors.primaryColor)
                            .padding(vertical = 10.dp, horizontal = 30.dp)
                    )

                    Text(
                        text = stringResource(id = R.string.long_click_for_more_tool),
                        style = TextStyle(
                            fontSize = 15.sp,
                            color = Color.White
                        ),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 140.dp)
                            .background(Color.Black.copy(0.5f), shape = RoundedCornerShape(50))
                            .padding(10.dp)
                    )
                }
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
        style = TitleTextStyle,
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
                indication = ripple(radius = 25.dp, bounded = false),
            ) { navController.popBackStack() },
        colorFilter = ColorFilter.tint(TitleTextStyle.color)
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
        WebToolWidget(
            toolList = listOf(
                WebToolWidgetEnum.Collect,
                WebToolWidgetEnum.Browser,
                WebToolWidgetEnum.Share
            ),
            openProvide = {
                true
            },
            onOpenChanged = {

            },
            loadingProgressProvider = { 10 }
        )
    }

}

enum class WebToolWidgetEnum {

    Share,
    Browser,
    Collect,
    Back

}

@Composable
private fun WebToolWidget(
    modifier: Modifier = Modifier,
    openProvide: () -> Boolean,
    onOpenChanged: (Boolean) -> Unit,
    loadingProgressProvider: () -> Int,
    toolList: List<WebToolWidgetEnum>,
    isCollect: Boolean = false,
    onClick: (WebToolWidgetEnum) -> Unit = {},
    onCloseButtonPositionChanged: (Offset) -> Unit = {},
) {

    val openValue = openProvide()

    val openTransition = updateTransition(targetState = openValue, label = "")
    val alpha by openTransition.animateFloat(label = "alpha") { isOpen ->
        if (isOpen) 1f else 0f
    }

    val rotate by openTransition.animateFloat(label = "") { isOpen ->
        if (isOpen) 180f else 0f
    }

    Box(modifier.size(60.dp), contentAlignment = Alignment.Center) {
        AnimatedVisibility(
            enter = fadeIn(),
            exit = fadeOut(),
            visible = loadingProgressProvider() < 100
        ) {
            CircularProgressIndicator(
                progress = { loadingProgressProvider() / 100f },
                modifier = Modifier.size(60.dp),
                color = WanTheme.colors.tipColor,
                trackColor = Color.Transparent
            )
        }
        toolList.forEachIndexed { index, enum ->
            key(enum) {
                val offset by openTransition.animateDp(label = enum.toString()) { isOpen ->
                    if (isOpen) -(buttonSize + 10.dp) * (index + 1) else 0.dp
                }
                WebToolButton(
                    Modifier
                        .offset(y = offset)
                        .alpha(alpha)
                        .clickable(
                            enabled = openValue,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(
                                radius = 25.dp
                            )
                        ) { onClick(enum) },
                    resId = when (enum) {
                        WebToolWidgetEnum.Share -> R.drawable.ic_share
                        WebToolWidgetEnum.Browser -> R.drawable.ic_net
                        WebToolWidgetEnum.Collect -> {
                            if (isCollect) R.drawable.ic_like2 else R.drawable.ic_like
                        }

                        WebToolWidgetEnum.Back -> error("")
                    },
                    contentColorFilter = if (enum == WebToolWidgetEnum.Collect && isCollect) ColorFilter.tint(
                        CollectColor
                    ) else ColorFilter.tint(
                        Color.Black
                    ),
                )
            }

        }
        WebToolButton(
            modifier = Modifier
                .onGloballyPositioned { onCloseButtonPositionChanged(it.positionInRoot()) }
                .graphicsLayer {
                    this.rotationZ = rotate
                }
                .combinedClickable(
                    enabled = openTransition.isRunning.not(),
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(
                        radius = 25.dp
                    ),
                    onLongClick = {
                        onOpenChanged(!openValue)
                    },
                    onClick = {
                        if (openValue) {
                            onOpenChanged(false)
                        } else {
                            onClick(WebToolWidgetEnum.Back)
                        }
                    }
                ),
            backgroundColor = if (isCollect) CollectColor else Color.White,
            contentColorFilter = if (isCollect) ColorFilter.tint(Color.White) else ColorFilter.tint(
                Color.Black
            ),
            resId = if (openValue) R.drawable.ic_close else R.drawable.ic_direction_left
        )

    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewWebToolButton() {
    WebToolButton(
        resId = R.drawable.ic_close,
        backgroundColor = CollectColor,
        contentColorFilter = ColorFilter.tint(Color.White)
    )
}

private val buttonSize = 50.dp

@Composable
private fun WebToolButton(
    modifier: Modifier = Modifier,
    @DrawableRes resId: Int,
    backgroundColor: Color = Color.White,
    contentColorFilter: ColorFilter = ColorFilter.tint(Color.Black),
) {
    Surface(
        modifier.size(buttonSize),
        shape = CircleShape,
        shadowElevation = 2.dp,
        color = backgroundColor
    ) {
        Image(
            painter = painterResource(id = resId),
            contentDescription = null,
            modifier = Modifier
                .padding(15.dp),
            contentScale = ContentScale.Crop,
            colorFilter = contentColorFilter
        )
    }
}
