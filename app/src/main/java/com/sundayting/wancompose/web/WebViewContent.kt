package com.sundayting.wancompose.web

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.sundayting.wancompose.R
import com.sundayting.wancompose.WanComposeDestination
import com.sundayting.wancompose.common.ui.title.TitleBar
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Stable
class WebState(
    val url: String,
)

@Composable
fun rememberWebState(
    url: String,
): WebState {
    return remember { WebState(url) }
}

object WebViewScreen : WanComposeDestination {
    override val route: String
        get() = "浏览器"

    const val urlArg = "urlKey"

    val routeWithArgs = "${route}/{${urlArg}}"

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
        state: WebState,
        navController: NavController,
    ) {

        Column(
            modifier.navigationBarsPadding()
        ) {

            var title by remember { mutableStateOf("") }

            WebTitle(
                title = title,
                onClickClose = navController::popBackStack
            )

            AndroidView(modifier = Modifier.fillMaxSize(), factory = {
                val client = object : WebViewClient() {
                    override fun onPageFinished(view: WebView, url: String) {
                        title = view.title.orEmpty()
                    }
                }
                WebView(it).apply {
                    webViewClient = client
                }
            }, update = {
                it.loadUrl(state.url)
            })
        }


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

