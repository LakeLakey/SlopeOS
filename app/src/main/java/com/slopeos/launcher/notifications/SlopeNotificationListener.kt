package com.slopeos.launcher.notifications

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Simple notification listener to feed the custom notifications drawer. */
class SlopeNotificationListener : NotificationListenerService() {
    companion object {
        private val _notifs = MutableStateFlow<List<StatusBarNotification>>(emptyList())
        val notifications = _notifs.asStateFlow()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        _notifs.value = activeNotifications?.toList() ?: emptyList()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        _notifs.value = activeNotifications?.toList() ?: emptyList()
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        _notifs.value = activeNotifications?.toList() ?: emptyList()
    }
}
