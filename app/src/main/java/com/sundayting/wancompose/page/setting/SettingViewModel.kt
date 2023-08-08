package com.sundayting.wancompose.page.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.EvenManager
import com.sundayting.wancompose.common.event.ToastEvent
import com.sundayting.wancompose.network.okhttp.isNSuccess
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mineRepository: MineRepository,
    private val eventManager: EvenManager,
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    fun logout() {
        viewModelScope.launch {
            isLoading = true
            val result = mineRepository.logout()
            if (result.isNSuccess()) {
                mineRepository.clearLoginUser()
            } else {
                eventManager.emitEvent(ToastEvent(result.failureReason.message))
            }
        }.apply {
            invokeOnCompletion {
                isLoading = false
            }
        }
    }

}