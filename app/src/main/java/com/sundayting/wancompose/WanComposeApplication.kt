package com.sundayting.wancompose

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WanComposeApplication : Application() {


    companion object {

        lateinit var instance: WanComposeApplication

    }

    override fun onCreate() {
        super.onCreate()
        // TODO: 迁移至WorkManager
//        MainScope().launch {
//            dataStoreCookieJar.clearWanExpireCookie()
//        }
        instance = this
    }


}