package com.sundayting.wancompose.common.helper

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

// TODO: 考虑mediaStoreHelper？ 
@Singleton
class ShareHelper @Inject constructor(
    @ApplicationContext context: Context
) {

    private val cacheDir = context.cacheDir

    fun shareBitmap(context: Context, bitmap: Bitmap) {
        val file = saveBitmapToFile(bitmap) ?: return
        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri)
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        context.startActivity(Intent.createChooser(shareIntent, "分享卡片"))
    }

    private fun saveBitmapToFile(bitmap: Bitmap): File? {
        runCatching {
            val targetFile = File(cacheDir, "shared_image.png")
            targetFile.deleteOnExit()
            targetFile.mkdir()
            FileOutputStream(targetFile).use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
            return targetFile
        }
        return null
    }

}