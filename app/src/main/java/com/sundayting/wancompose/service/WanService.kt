package com.sundayting.wancompose.service

import android.app.ForegroundServiceStartNotAllowedException
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.sundayting.wancompose.R
import com.sundayting.wancompose.common.service.ServiceIds.WAN_SERVICE_ID

class WanService : LifecycleService() {

    companion object {
        const val CHANNEL_ID = "wan_compose_test_channel"
        private const val CHANNEL_NAME = "玩Compose测试通知"
    }

    private fun buildNewNotification(): Notification {
        return NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setContentTitle("玩Compose测试通知")
            .setContentText("运行中...")
            .setOngoing(true)
            .setSmallIcon(R.drawable.modern_android_logo)
            .build()
    }

    private fun startForeground() {
        try {
            createNotificationChannel()
            ServiceCompat.startForeground(
                this,
                WAN_SERVICE_ID,
                buildNewNotification(),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                } else {
                    0
                }
            )

        } catch (e: Exception) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is ForegroundServiceStartNotAllowedException) {
                Log.e("服务错误", "${e.message}")
            }
        }
    }

    private fun createNotificationChannel() {
        NotificationManagerCompat.from(this).createNotificationChannel(
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
        startForeground()

    }


}