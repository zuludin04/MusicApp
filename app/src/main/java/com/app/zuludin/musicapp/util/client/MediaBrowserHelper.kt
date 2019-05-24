package com.app.zuludin.musicapp.util.client

import android.content.ComponentName
import android.content.Context
import android.os.RemoteException
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.media.MediaBrowserServiceCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log

class MediaBrowserHelper(
    private val context: Context,
    private val serviceClass: Class<out MediaBrowserServiceCompat>
) {

    private var mediaBrowser: MediaBrowserCompat? = null
    private var mediaController: MediaControllerCompat? = null

    private var mediaBrowserConnectionCallback: MediaBrowserConnectionCallback
    private var mediaBrowserSubscriptionCallback: MediaBrowserSubscriptionCallback
    private var mediaControllerCallback: MediaControllerCallback
    private lateinit var mediaBrowserHelperCallback: MediaBrowserHelperCallback

    private var wasConfigurationChange = false

    init {
        mediaBrowserConnectionCallback = MediaBrowserConnectionCallback()
        mediaBrowserSubscriptionCallback = MediaBrowserSubscriptionCallback()
        mediaControllerCallback = MediaControllerCallback()
    }

    fun setMediaBrowserHelperCallback(callback: MediaBrowserHelperCallback) {
        mediaBrowserHelperCallback = callback
    }

    private inner class MediaControllerCallback : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat) {
            Log.d(TAG, "onMetadataChanged: CALLED")
             if (mediaBrowserHelperCallback != null) {
                 mediaBrowserHelperCallback.onMetadataChanged(metadata)
            }
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            Log.d(TAG, "onPlaybackStateChanged: CALLED")
             if (mediaBrowserHelperCallback != null) {
                 mediaBrowserHelperCallback.onPlaybackStateChanged(state!!)
            }
        }

        override fun onSessionDestroyed() {
            onPlaybackStateChanged(null)
        }
    }

    fun subscribeToNewPlaylist(currentPlaylistId: String, newPlaylistId: String) {
        if (currentPlaylistId != "") {
            mediaBrowser?.unsubscribe(currentPlaylistId)
        }
        mediaBrowser?.subscribe(newPlaylistId, mediaBrowserSubscriptionCallback)
    }

    fun onStart(wasConfigurationChange: Boolean) {
        this.wasConfigurationChange = wasConfigurationChange
        if (mediaBrowser == null) {
            mediaBrowser = MediaBrowserCompat(context,
                ComponentName(context, serviceClass),
                mediaBrowserConnectionCallback,
                null
            )
            mediaBrowser?.connect()
        }
        Log.d(TAG, "onStart: CALLED: Creating MediaBrowser, and connecting")
    }

    fun onStop() {
        if (mediaController != null) {
            mediaController?.unregisterCallback(mediaControllerCallback)
            mediaController = null
        }
        if (mediaBrowser != null && mediaBrowser?.isConnected!!) {
            mediaBrowser?.disconnect()
            mediaBrowser = null
        }
        Log.d(TAG, "onStop: CALLED: Releasing MediaController, Disconnecting from MediaBrowser")
    }

    private inner class MediaBrowserConnectionCallback : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.d(TAG, "onConnected: CALLED")
            try {
                mediaController = MediaControllerCompat(context, mediaBrowser?.sessionToken!!)
                mediaController?.registerCallback(mediaControllerCallback)
            } catch (e: RemoteException) {
                Log.d(TAG, String.format("onConnected: Problem: %s", e.toString()))
                throw RuntimeException(e)
            }
            mediaBrowser?.subscribe(mediaBrowser?.root!!, mediaBrowserSubscriptionCallback)
            Log.d(TAG, "onConnected: CALLED: subscribing to: " + mediaBrowser?.root)
        }
    }

    inner class MediaBrowserSubscriptionCallback : MediaBrowserCompat.SubscriptionCallback() {
        override fun onChildrenLoaded(parentId: String, children: MutableList<MediaBrowserCompat.MediaItem>) {
            Log.d(TAG, "onChildrenLoaded: CALLED: $parentId, $children")

            if (!wasConfigurationChange) {
                for (mediaItem in children) {
                    Log.d(TAG, "onChildrenLoaded: CALLED: queue item: " + mediaItem.mediaId!!)
                    mediaController?.addQueueItem(mediaItem.description)
                }
            }
        }
    }

    fun getTransportControls(): MediaControllerCompat.TransportControls {
        if (mediaController == null) {
            Log.d(TAG, "getTransportControls: MediaController is null!")
            throw IllegalStateException("MediaController is null!")
        }
        return mediaController?.transportControls!!
    }

    companion object {
        private const val TAG = "MediaBrowserHelper"
    }
}