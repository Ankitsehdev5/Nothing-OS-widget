package com.example.nothingmusicwidget

import android.content.ComponentName
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState

data class TrackInfo(
    val title: String,
    val artist: String,
    val albumArt: Bitmap?,
    val isPlaying: Boolean
)

object MediaControllerHelper {

    fun getActiveController(context: Context): MediaController? {
        return try {
            val manager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
            val component = ComponentName(context, MediaListenerService::class.java)
            val controllers = manager.getActiveSessions(component)
            controllers.firstOrNull {
                it.playbackState?.state == PlaybackState.STATE_PLAYING
            } ?: controllers.firstOrNull()
        } catch (_: SecurityException) {
            null
        }
    }

    fun getTrackInfo(context: Context): TrackInfo {
        val controller = getActiveController(context)
            ?: return TrackInfo(
                title = "NO MEDIA PLAYING",
                artist = "TAP TO OPEN PLAYER",
                albumArt = null,
                isPlaying = false
            )

        val metadata = controller.metadata
        val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE) ?: "UNKNOWN TRACK"
        val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST) ?: "UNKNOWN ARTIST"
        val art = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
            ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
        val isPlaying = controller.playbackState?.state == PlaybackState.STATE_PLAYING

        return TrackInfo(
            title = title.uppercase(),
            artist = artist.uppercase(),
            albumArt = art,
            isPlaying = isPlaying
        )
    }

    fun togglePlayPause(context: Context) {
        val controller = getActiveController(context) ?: return
        val isPlaying = controller.playbackState?.state == PlaybackState.STATE_PLAYING
        if (isPlaying) {
            controller.transportControls.pause()
        } else {
            controller.transportControls.play()
        }
    }

    fun skipToNext(context: Context) {
        getActiveController(context)?.transportControls?.skipToNext()
    }

    fun skipToPrevious(context: Context) {
        getActiveController(context)?.transportControls?.skipToPrevious()
    }
}
