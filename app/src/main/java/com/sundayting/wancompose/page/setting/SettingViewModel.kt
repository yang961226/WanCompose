package com.sundayting.wancompose.page.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mineRepository: MineRepository,
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    fun logout() {
//        viewModelScope.launch {
//            isLoading = true
//            val result = mineRepository.logout()
//            if (result.isNSuccess()) {
//                mineRepository.clearLoginUser()
//            } else {
//                EventManager.emitEvent(ToastEvent(result.failureReason.message))
//            }
//        }.apply {
//            invokeOnCompletion {
//                isLoading = false
//            }
//        }
    }

}