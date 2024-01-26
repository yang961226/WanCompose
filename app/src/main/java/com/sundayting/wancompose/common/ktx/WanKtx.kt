package com.sundayting.wancompose.common.ktx

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Build

fun Picture.saveToBitmap(): Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
    Bitmap.createBitmap(this)
} else {
    val bitmap = Bitmap.createBitmap(
        width,
        height,
        Bitmap.Config.ARGB_8888
    )
    val canvas = android.graphics.Canvas(bitmap)
    canvas.drawColor(android.graphics.Color.WHITE)
    canvas.drawPicture(this)
    bitmap
}