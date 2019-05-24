package com.app.zuludin.musicapp.util.player

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.support.v4.media.MediaMetadataCompat

abstract class PlayerAdapter(
    private val context: Context
) {

    private val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var audioFocusHelper: AudioFocusHelper = AudioFocusHelper()
    private var playOnAudioFocus = false

    fun play() {
        if (audioFocusHelper.requestAudioFocus()) {
            registerAudioNoisyReceiver()
            onPlay()
        }
    }

    fun stop() {
        audioFocusHelper.abandonAudioFocus()
        unregisterAudioNoisyReceiver()
        onStop()
    }

    fun pause() {
        if (!playOnAudioFocus) {
            audioFocusHelper.abandonAudioFocus()
            unregisterAudioNoisyReceiver()
            onPause()
        }
    }

    protected abstract fun onPlay()

    protected abstract fun onPause()

    abstract fun playFromMedia(metadata: MediaMetadataCompat)

    abstract fun getCurrentMedia(): MediaMetadataCompat

    abstract fun isPlaying(): Boolean

    protected abstract fun onStop()

    abstract fun seekTo(position: Long)

    abstract fun setVolume(volume: Float)

    private val AUDIO_NOISY_INTENT_FILTER = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)

    private var audioNoisyReceiverRegister = false

    private val audioNoisyReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent.action) {
                if (isPlaying()) {
                    pause()
                }
            }
        }
    }

    private fun registerAudioNoisyReceiver() {
        if (!audioNoisyReceiverRegister) {
            context.registerReceiver(audioNoisyReceiver, AUDIO_NOISY_INTENT_FILTER)
            audioNoisyReceiverRegister = true
        }
    }

    private fun unregisterAudioNoisyReceiver() {
        if (audioNoisyReceiverRegister) {
            context.unregisterReceiver(audioNoisyReceiver)
            audioNoisyReceiverRegister = false
        }
    }

    private inner class AudioFocusHelper : AudioManager.OnAudioFocusChangeListener {
        fun requestAudioFocus(): Boolean {
            val result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
        }

        fun abandonAudioFocus() {
            audioManager.abandonAudioFocus(this)
        }

        override fun onAudioFocusChange(focusChange: Int) {
            when (focusChange) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    if (playOnAudioFocus && !isPlaying()) {
                        play()
                    } else if (isPlaying()) {
                        setVolume(MEDIA_VOLUME_DEFAULT)
                    }
                    playOnAudioFocus = false
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                    setVolume(MEDIA_VOLUME_DUCK)
                }
                AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                    if (isPlaying()) {
                        playOnAudioFocus = true
                        pause()
                    }
                }
                AudioManager.AUDIOFOCUS_LOSS -> {
                    audioManager.abandonAudioFocus(this)
                    playOnAudioFocus = false
                    pause()
                }
            }
        }
    }

    companion object {
        private const val MEDIA_VOLUME_DEFAULT = 1.0f
        private const val MEDIA_VOLUME_DUCK = 0.2f
    }
}