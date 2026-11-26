package com.example.nothingmusicwidget

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService

class MediaListenerService : NotificationListenerService() {

    private var sessionManager: MediaSessionManager? = null
    private var activeController: MediaController? = null

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            MusicWidgetProvider.updateAllWidgets(applicationContext)
        }

        override fun onMetadataChanged(metadata: MediaMetadata?) {
            MusicWidgetProvider.updateAllWidgets(applicationContext)
        }
    }

    private val sessionsChangedListener =
        MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
            registerController(controllers?.firstOrNull())
            MusicWidgetProvider.updateAllWidgets(applicationContext)
        }

    override fun onListenerConnected() {
        super.onListenerConnected()
        sessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val component = ComponentName(this, MediaListenerService::class.java)
        try {
            sessionManager?.addOnActiveSessionsChangedListener(sessionsChangedListener, component)
            val controllers = sessionManager?.getActiveSessions(component)
            registerController(controllers?.firstOrNull())
        } catch (_: SecurityException) {
        }
        MusicWidgetProvider.updateAllWidgets(applicationContext)
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        activeController?.unregisterCallback(controllerCallback)
        sessionManager?.removeOnActiveSessionsChangedListener(sessionsChangedListener)
    }

    private fun registerController(controller: MediaController?) {
        activeController?.unregisterCallback(controllerCallback)
        activeController = controller
        activeController?.registerCallback(controllerCallback)
    }
}
