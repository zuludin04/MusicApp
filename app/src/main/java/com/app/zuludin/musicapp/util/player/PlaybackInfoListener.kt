package com.app.zuludin.musicapp.util.player

import android.support.v4.media.session.PlaybackStateCompat

interface PlaybackInfoListener {
    fun onPlaybackStateChange(state: PlaybackStateCompat)

    fun onSeekTo(progress: Long, max: Long)

    fun onPlaybackComplete()

    fun updateUI(newMediaId: String)
}