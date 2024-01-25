package com.sundayting.wancompose.common.helper

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.DEFAULT_AMPLITUDE
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.runtime.staticCompositionLocalOf
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

val LocalVibratorHelper = staticCompositionLocalOf<VibratorHelper> {
    error("丢你螺母，还没传值呢")
}

/**
 * 震动小帮手
 */
@Singleton
class VibratorHelper @Inject constructor(
    @ApplicationContext context: Context,
) {

    private val vibrator by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    companion object {

        const val NORMAL_VIBRATE = 10L
        const val SMALL_VIBRATE = 5L

    }

    @Suppress("DEPRECATION")
    fun vibrateClick(
        millSec: Long = NORMAL_VIBRATE,
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(millSec, DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(millSec)
        }
    }

}