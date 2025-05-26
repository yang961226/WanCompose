package com.sundayting.wancompose

import android.app.Application
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class WanComposeApplication : Application() {


    companion object {

        lateinit var instance: WanComposeApplication

    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        instance = this
    }


}