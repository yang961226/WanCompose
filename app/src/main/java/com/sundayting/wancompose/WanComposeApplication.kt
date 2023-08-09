package com.sundayting.wancompose

import android.app.Application
import com.sundayting.wancompose.network.okhttp.cookie.DataStoreCookieJar
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class WanComposeApplication : Application() {

    @Inject
    lateinit var dataStoreCookieJar: DataStoreCookieJar

    companion object {

        lateinit var instance: WanComposeApplication

    }

    override fun onCreate() {
        super.onCreate()
        // TODO: 迁移至WorkManager
        MainScope().launch {
            dataStoreCookieJar.clearWanExpireCookie()
        }
        instance = this
    }


}