package com.slopeos.launcher.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.sqrt

/** Computes a simple luminance metric from the current wallpaper to drive blur/glow. */
object WallpaperUtils {
    fun wallpaperLuminance(context: Context): Float {
        return try {
            val wm = WallpaperManager.getInstance(context)
            val dw = wm.drawable ?: return 0.5f
            val bmp = dw.toBitmap(64, 64)
            averageLuma(bmp)
        } catch (_: Exception) { 0.5f }
    }

    private fun averageLuma(bitmap: Bitmap): Float {
        val w = bitmap.width
        val h = bitmap.height
        var sum = 0.0
        val pixels = IntArray(w * h)
        bitmap.getPixels(pixels, 0, w, 0, 0, w, h)
        for (p in pixels) {
            val r = (p shr 16 and 0xFF) / 255.0
            val g = (p shr 8 and 0xFF) / 255.0
            val b = (p and 0xFF) / 255.0
            // Perceived luminance
            sum += 0.2126 * r + 0.7152 * g + 0.0722 * b
        }
        return (sum / pixels.size).toFloat().coerceIn(0f, 1f)
    }
}
