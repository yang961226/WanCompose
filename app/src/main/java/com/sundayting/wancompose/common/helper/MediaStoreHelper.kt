package com.sundayting.wancompose.common.helper

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class MediaStoreHelper @Inject constructor() {

    suspend fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        displayName: String? = null,
    ) {
        saveBitmap(
            contentResolver = context.contentResolver,
            bitmap = bitmap,
            displayName = displayName
        )
    }

    suspend fun saveBitmap(
        contentResolver: ContentResolver,
        bitmap: Bitmap,
        displayName: String? = null,
    ) {
        suspendCancellableCoroutine {
            try {
                val realDisplayName =
                    displayName?.replace(".png", "") ?: System.currentTimeMillis().toString()
                val values = ContentValues().apply {
                    //设置文件的 MimeType
                    put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                    //指定保存的文件名，
                    put(MediaStore.Images.Media.DISPLAY_NAME, realDisplayName)
                }
                //插入文件数据库并获取到文件的Uri
                val insertUri =
                    contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                if (insertUri != null) {
                    //通过outputStream将图片文件内容写入Url
                    contentResolver.openOutputStream(insertUri)?.use { outputStream ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                        outputStream.flush()
                    }
                }
            } catch (e: Exception) {
                it.cancel(e)
            }
            it.resume(Unit)
        }
    }

}