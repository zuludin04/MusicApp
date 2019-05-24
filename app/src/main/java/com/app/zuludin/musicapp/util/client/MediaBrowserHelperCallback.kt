package com.app.zuludin.musicapp.util.client

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat

interface MediaBrowserHelperCallback {
    fun onMetadataChanged(metadata: MediaMetadataCompat)

    fun onPlaybackStateChanged(state: PlaybackStateCompat)

    fun onMediaControllerConnected(mediaController: MediaControllerCompat)
}









