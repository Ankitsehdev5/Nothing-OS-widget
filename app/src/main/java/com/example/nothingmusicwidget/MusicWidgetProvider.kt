package com.example.nothingmusicwidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews

class MusicWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                MediaControllerHelper.togglePlayPause(context)
                updateAllWidgets(context)
            }
            ACTION_NEXT -> {
                MediaControllerHelper.skipToNext(context)
                updateAllWidgets(context)
            }
            ACTION_PREV -> {
                MediaControllerHelper.skipToPrevious(context)
                updateAllWidgets(context)
            }
        }
    }

    companion object {
        const val ACTION_PLAY_PAUSE = "com.example.nothingmusicwidget.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.example.nothingmusicwidget.ACTION_NEXT"
        const val ACTION_PREV = "com.example.nothingmusicwidget.ACTION_PREV"

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, MusicWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            for (id in ids) {
                updateWidget(context, manager, id)
            }
            manager.notifyAppWidgetViewDataChanged(ids, R.id.flipper_track)
        }

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val views = RemoteViews(context.packageName, R.layout.widget_nothing_music)
            val track = MediaControllerHelper.getTrackInfo(context)

            if (track.albumArt != null) {
                views.setImageViewBitmap(R.id.iv_album_art, track.albumArt)
            } else {
                views.setImageViewResource(R.id.iv_album_art, R.drawable.ic_nothing_dots)
            }

            // Only Play/Pause toggle button is visibly shown
            views.setImageViewResource(
                R.id.btn_play_pause,
                if (track.isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
            views.setOnClickPendingIntent(
                R.id.btn_play_pause,
                getBroadcastIntent(context, ACTION_PLAY_PAUSE, 1)
            )

            // Swipeable AdapterViewFlipper for track info + left/right edge zones
            val flipperIntent = Intent(context, WidgetFlipperService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }
            views.setRemoteAdapter(R.id.flipper_track, flipperIntent)

            views.setOnClickPendingIntent(
                R.id.zone_prev,
                getBroadcastIntent(context, ACTION_PREV, 2)
            )
            views.setOnClickPendingIntent(
                R.id.zone_next,
                getBroadcastIntent(context, ACTION_NEXT, 3)
            )

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun getBroadcastIntent(
            context: Context,
            action: String,
            requestCode: Int
        ): PendingIntent {
            val intent = Intent(context, MusicWidgetProvider::class.java).apply {
                this.action = action
            }
            return PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }
    }
}
