package com.slopeos.launcher.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

fun Drawable.toImageBitmap(): ImageBitmap {
    val bmp = if (intrinsicWidth > 0 && intrinsicHeight > 0) {
        Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    } else {
        Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888)
    }
    val canvas = Canvas(bmp)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bmp.asImageBitmap()
}
