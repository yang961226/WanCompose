package com.sundayting.wancompose.page.share

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.ShareArticleSuccess
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.myshare.ShareArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val repo: ShareArticleRepository,
    private val eventManager: EventManager,
) : ViewModel() {

    val state = ShareUiState()

    fun onTitleInputChanged(text: String) {
        state.titleInput = text.trim()
    }

    fun onLinkInputChanged(text: String) {
        state.linkInput = text.replace(" ", "")
    }

    private fun String.isValidHttp(): Boolean {
        return startsWith("http://") || startsWith("https://")
    }

    fun shareArticle() {
        if (!state.linkInput.isValidHttp()) {
            eventManager.emitToast(R.string.wrong_http)
            return
        }
        state.isLoading = true
        viewModelScope.launch {
            if (repo.shareArticle(state.titleInput, state.linkInput).isSuccess()) {
                repo.cachedArticleListSuccess = false
                repo.cachedArticleList.clear()
                eventManager.emitEvent(ShareArticleSuccess)
                state.needBack = true
            }
        }.invokeOnCompletion { state.isLoading = false }
    }

    @Stable
    class ShareUiState {

        var isLoading by mutableStateOf(false)

        var titleInput by mutableStateOf("")

        var linkInput by mutableStateOf("")

        var needBack by mutableStateOf(false)

    }

}

