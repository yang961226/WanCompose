package com.sundayting.wancompose.page.webscreen

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShowLoginPageEvent
import com.sundayting.wancompose.common.event.emitCollectArticleEvent
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.common.helper.MediaStoreHelper
import com.sundayting.wancompose.common.helper.ShareHelper
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.homescreen.article.repo.ArticleRepository
import com.sundayting.wancompose.page.homescreen.article.ui.ArticleList
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val articleRepo: ArticleRepository,
    private val mineRepo: MineRepository,
    private val eventManager: EventManager,
    private val dataStore: DataStore<Preferences>,
    private val mediaStoreHelper: MediaStoreHelper,
    private val shareHelper: ShareHelper,
    @ApplicationContext context: Context
) : ViewModel() {

    companion object {

        private val IS_SHOW_GUIDE_KEY = booleanPreferencesKey("是否显示过新手引导")

    }

    private val contentResolver = context.contentResolver

    val webViewUiState = Json.decodeFromString<ArticleList.ArticleUiBean>(
        savedStateHandle.get<String>(WebViewScreen.ARGS_KEY)?.let {
            URLDecoder.decode(it, StandardCharsets.UTF_8.toString())
        } ?: error("没有参数！")
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

        var targetUrl by mutableStateOf(articleUiBean.link)

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
            val tryCollect = !articleUiBean.isCollect
            val success = if (tryCollect) {
                articleRepo.collectArticle(articleUiBean.id).isSuccess()
            } else {
                articleRepo.unCollectArticle(articleUiBean.id).isSuccess()
            }
            if (success) {
                webViewUiState.articleUiBean = webViewUiState.articleUiBean.copy(
                    isCollect = articleUiBean.isCollect.not()
                )
                eventManager.emitCollectArticleEvent(articleUiBean, tryCollect)
            }
        }

    }

    fun saveShareCard(imageBitmap: ImageBitmap) {
        viewModelScope.launch {
            mediaStoreHelper.saveBitmap(
                contentResolver,
                imageBitmap.asAndroidBitmap()
            )
            eventManager.emitToast(R.string.save_success)
        }
    }

    fun shareNow(context: Context, imageBitmap: ImageBitmap) {
        viewModelScope.launch {
            shareHelper.shareBitmap(context, imageBitmap.asAndroidBitmap())
        }
    }

}