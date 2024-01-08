package com.sundayting.wancompose.page.webscreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val url
        get() = savedStateHandle.get<String>(WebViewScreen.urlArg) ?: ""
    private val articleId
        get() = savedStateHandle.get<Long>(WebViewScreen.articleIdArg) ?: -1

    val webViewUiState = WebViewUiState(
        url = url,
        toolList = (if (articleId != -1L) listOf(WebToolWidgetEnum.Collect) else listOf()) + listOf(
            WebToolWidgetEnum.Browser,
            WebToolWidgetEnum.Share
        ),
        isCollect = savedStateHandle.get<Boolean>(WebViewScreen.isCollectArg) ?: false
    )

    @Stable
    class WebViewUiState(
        url: String,
        toolList: List<WebToolWidgetEnum>,
        isCollect: Boolean,
    ) {

        var isCollected by mutableStateOf(isCollect)

        val webViewState = WebViewState(WebContent.Url(url))

        private val _toolList = mutableStateListOf<WebToolWidgetEnum>().apply {
            addAll(toolList)
        }
        val toolList: List<WebToolWidgetEnum> = _toolList
    }

}