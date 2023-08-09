package com.sundayting.wancompose

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WanViewModel @Inject constructor(
    private val mineRepository: MineRepository,
) : ViewModel() {

    val curLoginUserFlow = mineRepository.curUserFlow

    val loginOrRegisterState = LoginOrRegisterState()

    @Stable
    class LoginOrRegisterState {

        var isLoading by mutableStateOf(false)

    }

    private var loginOrRegisterJog: Job? = null

    fun login(username: String, password: String) {
        loginOrRegisterJog?.cancel()
        loginOrRegisterJog = viewModelScope.launch {
            loginOrRegisterState.isLoading = true
            mineRepository.loginAndAutoInsertData(username, password)
        }.apply {
            invokeOnCompletion {
                loginOrRegisterState.isLoading = false
            }
        }
    }

    fun register(username: String, password: String, passwordAgain: String) {
        loginOrRegisterJog?.cancel()
        loginOrRegisterJog = viewModelScope.launch {
            loginOrRegisterState.isLoading = true
            val result = mineRepository.register(username, password, passwordAgain)
        }.apply {
            invokeOnCompletion {
                loginOrRegisterState.isLoading = false
            }
        }
    }

}