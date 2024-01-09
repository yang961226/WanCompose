package com.sundayting.wancompose.page.webscreen

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.web.WebContent
import com.google.accompanist.web.WebViewState
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val articleRepo: ArticleRepository,
    private val mineRepo: MineRepository,
    private val eventManager: EventManager,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    companion object {

        private val IS_SHOW_GUIDE_KEY = booleanPreferencesKey("是否显示过新手引导")

    }

    val webViewUiState = Json.decodeFromString<ArticleList.ArticleUiBean>(
        savedStateHandle.get<String>(WebViewScreen.argumentKey)
            ?: error("没有参数！")
    ).let {
        WebViewUiState(
            articleUiBean = it,
            toolList = (if (it.id != -1L) listOf(WebToolWidgetEnum.Collect) else listOf()) + listOf(
                WebToolWidgetEnum.Browser,
                WebToolWidgetEnum.Share
            ),
        )
    }

    init {
        viewModelScope.launch {
            val isShowGuide = dataStore.data.map { it[IS_SHOW_GUIDE_KEY] }.firstOrNull() ?: false
            if (!isShowGuide) {
                webViewUiState.needShowGuide = true
                dataStore.edit {
                    it[IS_SHOW_GUIDE_KEY] = true
                }
            }
        }
    }

    @Stable
    class WebViewUiState(
        articleUiBean: ArticleList.ArticleUiBean,
        toolList: List<WebToolWidgetEnum>,
    ) {

        var needShowGuide by mutableStateOf(false)

        var articleUiBean by mutableStateOf(articleUiBean)

        val webViewState = WebViewState(WebContent.Url(articleUiBean.link))

        private val _toolList = mutableStateListOf<WebToolWidgetEnum>().apply {
            addAll(toolList)
        }
        val toolList: List<WebToolWidgetEnum> = _toolList
    }

    private var changeCollectJob: Job? = null

    fun collectOrUnCollectArticle() {
        if (changeCollectJob?.isActive == true) {
            return
        }
        if (mineRepo.curUserFlow.value == null) {
            eventManager.emitEvent(ShowLoginPageEvent)
            return
        }
        changeCollectJob = viewModelScope.launch {
            val articleUiBean = webViewUiState.articleUiBean
            val success = if (articleUiBean.isCollect) {
                articleRepo.unCollectArticle(articleUiBean.id).isSuccess()
            } else {
                articleRepo.collectArticle(articleUiBean.id).isSuccess()
            }
            if (success) {
                webViewUiState.articleUiBean = webViewUiState.articleUiBean.copy(
                    isCollect = articleUiBean.isCollect.not()
                )
                eventManager.emitCollectArticleEvent(articleUiBean)
            }
        }

    }

}