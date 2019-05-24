package com.app.zuludin.musicapp

import android.app.Application
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import java.util.*

class MyApplication : Application() {

    private val mediaItems: MutableList<MediaBrowserCompat.MediaItem> = mutableListOf()
    private val treeMap: TreeMap<String, MediaMetadataCompat> = TreeMap()

    fun setMediaItems(items: List<MediaMetadataCompat>) {
        mediaItems.clear()
        for (media in items) {
            mediaItems.add(MediaBrowserCompat.MediaItem(media.description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE))
            treeMap[media.description.mediaId!!] = media
        }
    }

    fun getMediaItems(): MutableList<MediaBrowserCompat.MediaItem> = mediaItems

    fun getTreeMap(): TreeMap<String, MediaMetadataCompat> = treeMap

    fun getMediaItem(mediaId: String): MediaMetadataCompat {
        return treeMap[mediaId]!!
    }

    companion object {
        private var instance: MyApplication? = null

        fun getInstance(): MyApplication {
            if (instance == null) {
                instance = MyApplication()
            }
            return instance as MyApplication
        }
    }
}