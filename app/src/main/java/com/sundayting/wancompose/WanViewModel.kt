package com.sundayting.wancompose

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
import com.sundayting.wancompose.page.homescreen.mine.share.repo.MyCollectedArticleRepository
import com.sundayting.wancompose.page.myshare.MyShareArticleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WanViewModel @Inject constructor(
    private val mineRepository: MineRepository,
    private val myCollectedArticleRepository: MyCollectedArticleRepository,
    private val myShareArticleRepository: MyShareArticleRepository,
    val eventManager: EventManager,
) : ViewModel() {

    val curLoginUserFlow = mineRepository.curUserFlow
        .onEach {
            myCollectedArticleRepository.cachedArticleListSuccess = false
            myCollectedArticleRepository.cachedArticleList.clear()

            myShareArticleRepository.cachedArticleList.clear()
            myShareArticleRepository.cachedArticleListSuccess = false

            if (it != null) {
                eventManager.emitToast(isLong = true) { context ->
                    context.getString(R.string.welcome_back_tip, it.nick)
                }
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