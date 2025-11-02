package com.slopeos.launcher.data

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Data model representing an app entry (launchable activity). */
data class AppEntry(
    val label: String,
    val packageName: String,
    val className: String
) {
    fun launchIntent(pm: PackageManager): Intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
        setClassName(packageName, className)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}

/** Repository to load installed and launchable apps. */
class AppRepository(private val context: Context) {
    private val pm: PackageManager = context.packageManager

    suspend fun loadAllLaunchables(): List<AppEntry> = withContext(Dispatchers.Default) {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val activities = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        activities.mapNotNull { ri ->
            val ai = ri.activityInfo ?: return@mapNotNull null
            val label = ai.loadLabel(pm)?.toString() ?: ai.name
            AppEntry(label = label, packageName = ai.packageName, className = ai.name)
        }.filter { it.packageName != context.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun getAppIcon(entry: AppEntry): Drawable? = try {
        pm.getActivityIcon(ComponentName(entry.packageName, entry.className))
    } catch (_: Exception) {
        try { pm.getApplicationIcon(entry.packageName) } catch (_: Exception) { null }
    }
}
