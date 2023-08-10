package com.sundayting.wancompose.page.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.NetExceptionHandler
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mineRepository: MineRepository,
) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    fun logout() {
        viewModelScope.launch(NetExceptionHandler) {
            isLoading = true
            val result = mineRepository.logout()
            if (result.isSuccess()) {
                mineRepository.clearLoginUser()
            }
        }.apply {
            invokeOnCompletion {
                isLoading = false
            }
        }
    }

}