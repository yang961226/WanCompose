package com.sundayting.wancompose.page.setting

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundayting.wancompose.network.isSuccess
import com.sundayting.wancompose.page.homescreen.mine.repo.MineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val mineRepository: MineRepository,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    companion object {
        val openBannerKey = booleanPreferencesKey("首页打开Banner")
    }

    val openBannerFlow = dataStore.data.map { it[openBannerKey] ?: true }

    fun changedOpenBanner() {
        viewModelScope.launch {
            dataStore.edit {
                it[openBannerKey] = !(it[openBannerKey] ?: true)
            }
        }
    }

    var isLoading by mutableStateOf(false)
        private set

    fun logout() {
        viewModelScope.launch {
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