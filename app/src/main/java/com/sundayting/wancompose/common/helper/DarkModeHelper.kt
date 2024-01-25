package com.sundayting.wancompose.common.helper

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


val LocalDarkMode = staticCompositionLocalOf { false }
val LocalDarkModeFollowSystem = staticCompositionLocalOf { true }
val LocalSetToDarkMode = staticCompositionLocalOf { false }
val LocalDarkModeHelper = staticCompositionLocalOf<DarkModeHelper> { error("暂未赋值") }

@Singleton
class DarkModeHelper @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) {

    companion object {
        val DARK_MODE_FOLLOW_SYSTEM = booleanPreferencesKey("暗夜模式跟随系统")
        val DARK_MODE = booleanPreferencesKey("开启暗夜模式")
    }

    private val scope = CoroutineScope(SupervisorJob())

    val darkModeFollowSystemFlow = dataStore.data.mapLatest {
        it[DARK_MODE_FOLLOW_SYSTEM] ?: true
    }

    val darkModeSettingFlow = dataStore.data.mapLatest { it[DARK_MODE] ?: false }

    private var changeJob: Job? = null
    fun changeDarkModeFollowSystem(originModeIsDark: Boolean, tryFollow: Boolean) {
        if (changeJob?.isActive == true) {
            return
        }
        changeJob = scope.launch {
            dataStore.edit {
                it[DARK_MODE_FOLLOW_SYSTEM] = tryFollow
                it[DARK_MODE] = originModeIsDark
            }
        }
    }

    fun changeDarkModeSetting(isDarkMode: Boolean) {
        if (changeJob?.isActive == true) {
            return
        }
        changeJob = scope.launch {
            dataStore.edit {
                it[DARK_MODE] = isDarkMode
            }
        }
    }


}