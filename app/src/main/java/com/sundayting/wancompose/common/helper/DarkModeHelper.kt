package com.sundayting.wancompose.common.helper

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton


val LocalDarkMode = staticCompositionLocalOf { false }
val LocalDarkModeFollowSystem = compositionLocalOf { true }
val LocalSetToDarkMode = compositionLocalOf { false }
val LocalDarkModeHelper = compositionLocalOf<DarkModeHelper> { error("暂未赋值") }

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
    }.stateIn(scope, SharingStarted.Eagerly, true)

    val darkModeSettingFlow = dataStore.data.mapLatest { it[DARK_MODE] ?: false }.stateIn(
        scope,
        SharingStarted.Eagerly, false
    )

    init {
        scope.launch(Dispatchers.Main) {
            darkModeFollowSystemFlow.combine(darkModeSettingFlow) { followSystem, darkModeSetting ->
                followSystem to darkModeSetting
            }.collect {
                val followSystem = it.first
                val darkModeSetting = it.second
                if (followSystem) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                } else {
                    AppCompatDelegate.setDefaultNightMode(
                        if (darkModeSetting) {
                            AppCompatDelegate.MODE_NIGHT_YES
                        } else {
                            AppCompatDelegate.MODE_NIGHT_NO
                        }
                    )
                }
            }
        }
    }

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