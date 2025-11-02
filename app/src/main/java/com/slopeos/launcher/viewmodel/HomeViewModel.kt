package com.slopeos.launcher.viewmodel

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slopeos.launcher.data.AppEntry
import com.slopeos.launcher.data.AppRepository
import com.slopeos.launcher.data.SettingsDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val context: Context) : ViewModel() {
    private val repo = AppRepository(context)
    private val store = SettingsDataStore(context)

    val allApps = MutableStateFlow<List<AppEntry>>(emptyList())
    val pinned: StateFlow<Set<String>> = store.pinnedFlow.stateIn(viewModelScope, SharingStarted.Eagerly, emptySet())

    val unpinned = combine(allApps, pinned) { apps, pins ->
        apps.filterNot { pins.contains(it.packageName) }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun load() {
        viewModelScope.launch {
            allApps.value = repo.loadAllLaunchables()
        }
    }

    fun pin(pkg: String) = viewModelScope.launch { store.addPinned(pkg) }
    fun unpin(pkg: String) = viewModelScope.launch { store.removePinned(pkg) }

    fun openApp(entry: AppEntry) {
        try {
            context.startActivity(entry.launchIntent(context.packageManager))
        } catch (_: Exception) {}
    }

    fun showAppInfo(entry: AppEntry) {
        val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:${entry.packageName}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
}
