package com.slopeos.launcher.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val DS_NAME = "slope_settings"
val Context.dataStore by preferencesDataStore(DS_NAME)

object Keys {
    val Pinned = stringSetPreferencesKey("pinned")
    val IconSize = floatPreferencesKey("icon_size") // dp multiplier
    val Blur = floatPreferencesKey("blur")          // dp
    val Opacity = floatPreferencesKey("opacity")    // 0..1
    val Haptics = booleanPreferencesKey("haptics")
}

class SettingsDataStore(private val context: Context) {
    val pinnedFlow: Flow<Set<String>> = context.dataStore.data.map { it[Keys.Pinned] ?: emptySet() }
    val iconSizeFlow: Flow<Float> = context.dataStore.data.map { it[Keys.IconSize] ?: 1.0f }
    val blurFlow: Flow<Float> = context.dataStore.data.map { it[Keys.Blur] ?: 18f }
    val opacityFlow: Flow<Float> = context.dataStore.data.map { it[Keys.Opacity] ?: 0.35f }
    val hapticsFlow: Flow<Boolean> = context.dataStore.data.map { it[Keys.Haptics] ?: true }

    suspend fun setPinned(pkgs: Set<String>) = context.dataStore.edit { it[Keys.Pinned] = pkgs }
    suspend fun addPinned(pkg: String) = context.dataStore.edit { it[Keys.Pinned] = (it[Keys.Pinned] ?: emptySet()) + pkg }
    suspend fun removePinned(pkg: String) = context.dataStore.edit { it[Keys.Pinned] = (it[Keys.Pinned] ?: emptySet()) - pkg }

    suspend fun setIconSize(mult: Float) = context.dataStore.edit { it[Keys.IconSize] = mult }
    suspend fun setBlur(dp: Float) = context.dataStore.edit { it[Keys.Blur] = dp }
    suspend fun setOpacity(alpha: Float) = context.dataStore.edit { it[Keys.Opacity] = alpha }
    suspend fun setHaptics(enabled: Boolean) = context.dataStore.edit { it[Keys.Haptics] = enabled }

    suspend fun reset() = context.dataStore.edit {
        it[Keys.Pinned] = emptySet()
        it[Keys.IconSize] = 1.0f
        it[Keys.Blur] = 18f
        it[Keys.Opacity] = 0.35f
        it[Keys.Haptics] = true
    }
}
