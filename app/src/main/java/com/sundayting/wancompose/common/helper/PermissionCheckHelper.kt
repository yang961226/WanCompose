package com.sundayting.wancompose.common.helper

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sundayting.wancompose.common.helper.PermissionCheckHelper.PermissionStatus.Denied
import com.sundayting.wancompose.common.helper.PermissionCheckHelper.PermissionStatus.Granted
import com.sundayting.wancompose.common.helper.PermissionCheckHelper.PermissionStatus.PermanentDenied

object PermissionCheckHelper {

    /**
     * 权限状态
     * @property Granted 权限已同意
     * @property Denied 权限拒绝
     * @property PermanentDenied 用户选择不再询问
     */
    enum class PermissionStatus {

        Granted,
        Denied,
        PermanentDenied

    }


    /**
     * 检查权限状态
     */
    fun checkPermissionStatus(context: Context, permission: String): PermissionStatus {
        return if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied
        }
    }

    fun checkPermissionAfterRequest(context: Context, permission: String): PermissionStatus {
        return if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            PermissionStatus.Granted
        } else {
            return if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    context as Activity,
                    permission
                )
            ) {
                PermissionStatus.PermanentDenied
            } else {
                PermissionStatus.Denied
            }
        }
    }

}