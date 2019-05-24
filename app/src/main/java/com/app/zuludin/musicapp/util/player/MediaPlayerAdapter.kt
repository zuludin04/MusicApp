package com.app.zuludin.musicapp.util.player

import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.SystemClock
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.TrackGroupArray
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionArray
import com.google.android.exoplayer2.trackselection.TrackSelector
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class MediaPlayerAdapter(
    private val context: Context,
    private val playbackInfoListener: PlaybackInfoListener
) : PlayerAdapter(context) {

    private var currentMediaMetadata: MediaMetadataCompat? = null
    private var currentMediaPlayedToComplete = false
    private var state = 0
    private var startTime: Long = 0

    private var exoPlayer: SimpleExoPlayer? = null
    private var trackSelector: TrackSelector? = null
    private var rendererFactory: DefaultRenderersFactory? = null
    private var dataSourceFactory: DataSource.Factory? = null
    private var exoPlayerEventListener: ExoPlayerEventListener? = null

    private fun initializeExoPlayer() {
        if (exoPlayer == null) {
            trackSelector = DefaultTrackSelector()
            rendererFactory = DefaultRenderersFactory(context)
            dataSourceFactory = DefaultDataSourceFactory(context, Util.getUserAgent(context, "MusicApp"))
            exoPlayer = ExoPlayerFactory.newSimpleInstance(rendererFactory, trackSelector, DefaultLoadControl())

            if (exoPlayerEventListener == null) {
                exoPlayerEventListener = ExoPlayerEventListener()
            }

            exoPlayer?.addListener(exoPlayerEventListener)
        }
    }

    private fun release() {
        if (exoPlayer != null) {
            exoPlayer?.release()
            exoPlayer = null
        }
    }

    override fun onPlay() {
        if (exoPlayer != null && !exoPlayer?.playWhenReady!!) {
            exoPlayer?.playWhenReady = true
            setNewState(PlaybackStateCompat.STATE_PLAYING)
        }
    }

    override fun onPause() {
        if (exoPlayer != null && exoPlayer?.playWhenReady!!) {
            exoPlayer?.playWhenReady = false
            setNewState(PlaybackStateCompat.STATE_PAUSED)
        }
    }

    override fun playFromMedia(metadata: MediaMetadataCompat) {
        startTrackingPlayback()
        playFile(metadata)
    }

    override fun getCurrentMedia(): MediaMetadataCompat {
        return currentMediaMetadata!!
    }

    override fun isPlaying(): Boolean {
        return exoPlayer != null && exoPlayer?.playWhenReady!!
    }

    override fun onStop() {
        setNewState(PlaybackStateCompat.STATE_STOPPED)
        release()
    }

    override fun seekTo(position: Long) {
        if (exoPlayer != null) {
            exoPlayer?.seekTo(position)
            setNewState(state)
        }
    }

    override fun setVolume(volume: Float) {
        if (exoPlayer != null) {
            exoPlayer?.volume = volume
        }
    }

    private fun playFile(metadata: MediaMetadataCompat) {
        val mediaId = metadata.description.mediaId
        var mediaChanged = (currentMediaMetadata == null || !mediaId?.equals(currentMediaMetadata?.description?.mediaId)!!)

        if (currentMediaPlayedToComplete) {
            mediaChanged = true
            currentMediaPlayedToComplete = false
        }

        if (!mediaChanged) {
            if (!isPlaying()) {
                play()
            }
            return
        } else {
            release()
        }

        currentMediaMetadata = metadata

        initializeExoPlayer()

        try {
            val audioSource: MediaSource =
                ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)))
            exoPlayer?.prepare(audioSource)
        } catch (e: Exception) {
            throw RuntimeException(
                "Failed to play media uri: " + metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI), e)
        }

        play()
    }

    private fun startTrackingPlayback() {
        val handler = Handler()
        val runnable = object : Runnable {
            override fun run() {
                if (isPlaying()) {
                    playbackInfoListener.onSeekTo(exoPlayer?.contentPosition!!, exoPlayer?.duration!!)
                    handler.postDelayed(this, 100)
                }
/*                if (exoPlayer?.contentPosition?.toInt()!! >= exoPlayer?.duration?.toInt()!! && exoPlayer?.duration?.toInt()!! > 0) {
                    playbackInfoListener.onPlaybackComplete()
                }*/
            }
        }
        handler.postDelayed(runnable, 100)
    }

    private fun setNewState(@PlaybackStateCompat.State newPlayerState: Int) {
        state = newPlayerState
        if (state == PlaybackStateCompat.STATE_STOPPED) {
            currentMediaPlayedToComplete = true
        }
        val reportPosition: Long = if (exoPlayer == null) 0 else exoPlayer?.currentPosition!!
        publishStateBuilder(reportPosition)
    }

    private fun publishStateBuilder(reportPosition: Long) {
        val stateBuilder: PlaybackStateCompat.Builder = PlaybackStateCompat.Builder()
        stateBuilder.setActions(getAvailableActions())
        stateBuilder.setState(state, reportPosition, 1.0f, SystemClock.elapsedRealtime())
        playbackInfoListener.onPlaybackStateChange(stateBuilder.build())
//        playbackInfoListener.updateUI(currentMediaMetadata?.description?.mediaId!!)
    }

    @PlaybackStateCompat.Actions
    private fun getAvailableActions(): Long {
        var actions = (PlaybackStateCompat.ACTION_PLAY_FROM_MEDIA_ID
                or PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH
                or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)
        actions = when (state) {
            PlaybackStateCompat.STATE_STOPPED -> actions or (PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_PAUSE)
            PlaybackStateCompat.STATE_PLAYING -> actions or (PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE
                    or PlaybackStateCompat.ACTION_SEEK_TO)
            PlaybackStateCompat.STATE_PAUSED -> actions or (PlaybackStateCompat.ACTION_PLAY or PlaybackStateCompat.ACTION_STOP)
            else -> actions or (PlaybackStateCompat.ACTION_PLAY
                    or PlaybackStateCompat.ACTION_PLAY_PAUSE
                    or PlaybackStateCompat.ACTION_STOP
                    or PlaybackStateCompat.ACTION_PAUSE)
        }
        return actions
    }

    private inner class ExoPlayerEventListener : Player.EventListener {
        override fun onPlaybackParametersChanged(playbackParameters: PlaybackParameters?) {}

        override fun onSeekProcessed() {}

        override fun onTracksChanged(trackGroups: TrackGroupArray?, trackSelections: TrackSelectionArray?) {}

        override fun onPlayerError(error: ExoPlaybackException?) {}

        override fun onLoadingChanged(isLoading: Boolean) {}

        override fun onPositionDiscontinuity(reason: Int) {}

        override fun onRepeatModeChanged(repeatMode: Int) {}

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) { }

        override fun onTimelineChanged(timeline: Timeline?, manifest: Any?, reason: Int) {}

        override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
            when (playbackState) {
                Player.STATE_ENDED -> {
                    setNewState(PlaybackStateCompat.STATE_PAUSED)
                }
                Player.STATE_BUFFERING -> {
                    startTime = System.currentTimeMillis()
                }
                Player.STATE_IDLE -> {}
                Player.STATE_READY -> {
                    Log.d(TAG, "onPlayerStateChanged: READY")
                    Log.d(TAG, "onPlayerStateChanged: TIME ELAPSED: " + (System.currentTimeMillis() - startTime))
                }
            }
        }
    }

    companion object {
        private const val TAG = "MediaPlayerAdapter"
    }
}