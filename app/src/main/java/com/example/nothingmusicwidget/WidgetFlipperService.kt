package com.example.nothingmusicwidget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.RemoteViewsService

class WidgetFlipperService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TrackFlipperFactory(applicationContext)
    }
}

class TrackFlipperFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {

    private var trackInfo: TrackInfo = MediaControllerHelper.getTrackInfo(context)

    override fun onCreate() {
        trackInfo = MediaControllerHelper.getTrackInfo(context)
    }

    override fun onDataSetChanged() {
        trackInfo = MediaControllerHelper.getTrackInfo(context)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = 1

    override fun getViewAt(position: Int): RemoteViews {
        val views = RemoteViews(context.packageName, R.layout.widget_flipper_item)
        views.setTextViewText(R.id.tv_track_title, trackInfo.title)
        views.setTextViewText(R.id.tv_track_artist, trackInfo.artist)
        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}
