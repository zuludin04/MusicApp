package com.app.zuludin.musicapp.util.service

import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.content.ContextCompat
import androidx.media.MediaBrowserServiceCompat
import com.app.zuludin.musicapp.MyApplication
import com.app.zuludin.musicapp.util.player.MediaPlayerAdapter
import com.app.zuludin.musicapp.util.player.PlaybackInfoListener
import com.app.zuludin.musicapp.util.service.catalogs.MusicLibrary
import com.app.zuludin.musicapp.util.service.notifications.MediaNotificationManager

class MusicService : MediaBrowserServiceCompat() {

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var player: MediaPlayerAdapter
    private lateinit var sessionCallback: MediaSessionCallback
    private lateinit var myApplication: MyApplication
    private lateinit var mediaNotificationManager: MediaNotificationManager

    private var serviceInStartedState = false

    override fun onCreate() {
        super.onCreate()
        myApplication = MyApplication.getInstance()

        mediaSession = MediaSessionCompat(this, "MusicService")
        sessionCallback = MediaSessionCallback()

        mediaSession.setCallback(sessionCallback)
        mediaSession.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_QUEUE_COMMANDS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )

        sessionToken = mediaSession.sessionToken

        mediaNotificationManager = MediaNotificationManager(this)

        player = MediaPlayerAdapter(this, MediaPlayerListener())
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        sessionCallback.onStop()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        player.stop()
        mediaSession.release()
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot? {
        return BrowserRoot(MusicLibrary().getRoot(), null)
    }

    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(myApplication.getMediaItems())
    }

    inner class MediaSessionCallback : MediaSessionCompat.Callback() {
        private val playlist: MutableList<MediaSessionCompat.QueueItem> = mutableListOf()
        private var queueIndex = -1
        private var prepareMedia: MediaMetadataCompat? = null

        private fun resetPlaylist() {
            playlist.clear()
            queueIndex = -1
        }

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            resetPlaylist()

            prepareMedia = myApplication.getTreeMap()[mediaId]!!
            mediaSession.setMetadata(prepareMedia)
            if (!mediaSession.isActive) {
                mediaSession.isActive = true
            }
            player.playFromMedia(prepareMedia!!)

            val newQueuePosition = extras?.getInt("SONG_POSITION", -1)!!
            if (newQueuePosition == -1) {
                queueIndex++
            } else {
                queueIndex = newQueuePosition
            }
        }

        override fun onAddQueueItem(description: MediaDescriptionCompat) {
            playlist.add(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
            queueIndex = if (queueIndex == -1) 0 else queueIndex
            mediaSession.setQueue(playlist)
        }

        override fun onRemoveQueueItem(description: MediaDescriptionCompat) {
            playlist.remove(MediaSessionCompat.QueueItem(description, description.hashCode().toLong()))
            queueIndex = if (playlist.isEmpty()) -1 else queueIndex
            mediaSession.setQueue(playlist)
        }

        override fun onPrepare() {
            if (queueIndex < 0 && playlist.isEmpty()) {
                return
            }

            val mediaId = playlist[queueIndex].description.mediaId
            prepareMedia = myApplication.getTreeMap()[mediaId]!!
            mediaSession.setMetadata(prepareMedia)

            if (!mediaSession.isActive) {
                mediaSession.isActive = true
            }
        }

        override fun onPlay() {
            super.onPlay()
            if (!isReadyToPlay()) {
                return
            }

            if (prepareMedia == null) {
                onPrepare()
            }

            player.playFromMedia(prepareMedia!!)
        }

        override fun onPause() {
            player.pause()
        }

        override fun onStop() {
            player.stop()
            mediaSession.isActive = false
        }

        override fun onSkipToNext() {
            queueIndex = ++queueIndex % playlist.size
            prepareMedia = null
            onPlay()
        }

        override fun onSkipToPrevious() {
            queueIndex = if (queueIndex > 0) queueIndex - 1 else playlist.size - 1
            prepareMedia = null
            onPlay()
        }

        override fun onSeekTo(pos: Long) {
            player.seekTo(pos)
        }

        private fun isReadyToPlay(): Boolean = playlist.isNotEmpty()
    }

    inner class MediaPlayerListener : PlaybackInfoListener {

        private var serviceManager: ServiceManager

        init {
            serviceManager = ServiceManager()
        }

        override fun updateUI(newMediaId: String) {

        }

        override fun onPlaybackStateChange(state: PlaybackStateCompat) {
            mediaSession.setPlaybackState(state)
            when (state.state) {
                PlaybackStateCompat.STATE_PLAYING -> serviceManager.moveServiceToStartedState(state)
                PlaybackStateCompat.STATE_PAUSED -> serviceManager.updateNotificationForPause(state)
                PlaybackStateCompat.STATE_STOPPED -> serviceManager.moveServiceOutOfStartedState(state)
            }
        }

        override fun onSeekTo(progress: Long, max: Long) {

        }

        override fun onPlaybackComplete() {
            mediaSession.controller.transportControls.skipToNext()
        }

        internal inner class ServiceManager {
            fun moveServiceToStartedState(state: PlaybackStateCompat) {
                val notification = mediaNotificationManager.getNotification(
                    player.getCurrentMedia(), state, sessionToken!!
                )

                if (!serviceInStartedState) {
                    ContextCompat.startForegroundService(
                        this@MusicService,
                        Intent(this@MusicService, MusicService::class.java)
                    )
                    serviceInStartedState = true
                }

                startForeground(MediaNotificationManager.NOTIFICATION_ID, notification)
            }

            fun updateNotificationForPause(state: PlaybackStateCompat) {
                stopForeground(false)
                val notification = mediaNotificationManager.getNotification(
                    player.getCurrentMedia(), state, sessionToken!!
                )
                mediaNotificationManager.getNotificationManager()
                    .notify(MediaNotificationManager.NOTIFICATION_ID, notification)
            }

            fun moveServiceOutOfStartedState(state: PlaybackStateCompat) {
                stopForeground(true)
                stopSelf()
                serviceInStartedState = false
            }
        }
    }
}