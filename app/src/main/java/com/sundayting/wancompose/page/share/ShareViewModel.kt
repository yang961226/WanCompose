package com.sundayting.wancompose.page.share

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(

) : ViewModel() {

    val state = ShareUiState()

    fun onTitleInputChanged(text: String) {
        state.titleInput = text.replace(" ", "")
    }

    fun onLinkInputChanged(text: String) {
        state.linkInput = text.replace(" ", "")
    }

    fun shareArticle() {

    }

    @Stable
    class ShareUiState {

        var titleInput by mutableStateOf("")

        var linkInput by mutableStateOf("")

    }

}

