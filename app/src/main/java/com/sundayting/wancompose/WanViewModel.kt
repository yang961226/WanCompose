package com.sundayting.wancompose

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.common.event.EventManager
import com.sundayting.wancompose.common.event.emitToast
import com.sundayting.wancompose.network.NetExceptionHandler
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WanViewModel @Inject constructor(
    private val mineRepository: MineRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val curLoginUserFlow = mineRepository.curUserFlow
        .onEach {
            if (it != null) {
                EventManager.emitToast(
                    context.getString(R.string.welcome_back_tip, it.nick), true
                )
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            null
        )


    val loginOrRegisterState = LoginOrRegisterState()

    @Stable
    class LoginOrRegisterState {

        var isLoading by mutableStateOf(false)

    }

    private var loginOrRegisterJog: Job? = null

    fun login(username: String, password: String) {
        loginOrRegisterJog?.cancel()
        loginOrRegisterJog = viewModelScope.launch(NetExceptionHandler) {
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
        loginOrRegisterJog = viewModelScope.launch(NetExceptionHandler) {
            loginOrRegisterState.isLoading = true
            mineRepository.register(username, password, passwordAgain)
        }.apply {
            invokeOnCompletion {
                loginOrRegisterState.isLoading = false
            }
        }
    }

}