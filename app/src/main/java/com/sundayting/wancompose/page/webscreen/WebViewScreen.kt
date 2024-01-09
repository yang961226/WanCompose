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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.LoadingState
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewNavigator
import com.google.accompanist.web.rememberWebViewNavigator
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.common.helper.LocalVibratorHelper
import com.sundayting.wancompose.common.ui.title.TitleBar
import com.sundayting.wancompose.common.ui.title.TitleBarWithContent
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.theme.WanColors
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


object WebViewScreen : WanComposeDestination {
    override val route: String
        get() = "浏览器"

    const val argumentKey = "argumentKey"

    val routeWithArgs = "$route/{$argumentKey}"

    val arguments = listOf(
        navArgument(argumentKey) { type = NavType.StringType },
    )


    fun NavController.navigateToWebViewScreen(
        articleUiBean: ArticleList.ArticleUiBean,
    ) {
        navigate(
            "$route/${
                Json.encodeToString(
                    articleUiBean.copy(
                        link = URLEncoder.encode(
                            articleUiBean.link,
                            StandardCharsets.UTF_8.toString()
                        )
                    )
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
                    state = viewModel.webViewUiState.webViewState,
                    client = client
                )

                val vibratorHelper = LocalVibratorHelper.current

                WebToolWidget(
                    Modifier
                        .constrainAs(webToolContent) {
                            start.linkTo(parent.start, 30.dp)
                            bottom.linkTo(parent.bottom, 60.dp)
                        },
                    loadingProgress = remember {
                        derivedStateOf { (viewModel.webViewUiState.webViewState.loadingState as? LoadingState.Loading)?.progress }
                    }.value,
                    toolList = viewModel.webViewUiState.toolList,
                    isCollect = viewModel.webViewUiState.articleUiBean.isCollect,
                    openProvide = { isOpenToolBoxOpen },
                    onOpenChanged = {
                        if (it) {
                            vibratorHelper.vibrateLongClick()
                        }
                        isOpenToolBoxOpen = it
                    },
                    onClick = {
                        when (it) {
                            WebToolWidgetEnum.Share -> EventManager.getInstance()
                                .emitToast("开发中")

                            WebToolWidgetEnum.Browser -> context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse(viewModel.webViewUiState.webViewState.lastLoadedUrl)
                                )
                            )

                            WebToolWidgetEnum.Collect -> viewModel.collectOrUnCollectArticle()

                            WebToolWidgetEnum.Back -> if (navigator.canGoBack) {
                                navigator.navigateBack()
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
        }

        Crossfade(targetState = viewModel.webViewUiState.needShowGuide, label = "") { isVisible ->
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
                                indication = rememberRipple()
                            ) {
                                viewModel.webViewUiState.needShowGuide = false
                            }
                            .background(WanColors.TopColor)
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

            }
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
    loadingProgress: Float? = null,
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
            visible = loadingProgress != null && loadingProgress != 1f
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(60.dp),
                color = WanColors.TopColor,
                progress = loadingProgress ?: 0f,
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
                            indication = rememberRipple(
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
                        WanColors.CollectColor
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
                    indication = rememberRipple(
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
            backgroundColor = if (isCollect) WanColors.CollectColor else Color.White,
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
        backgroundColor = WanColors.CollectColor,
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
        elevation = 2.dp,
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

