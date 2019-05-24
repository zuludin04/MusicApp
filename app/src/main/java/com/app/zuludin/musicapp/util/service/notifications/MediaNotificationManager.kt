package com.app.zuludin.musicapp.util.service.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.app.NotificationCompat.MediaStyle
import androidx.media.session.MediaButtonReceiver
import com.app.zuludin.musicapp.util.service.MusicService
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.ui.main.MainActivity

class MediaNotificationManager {

    private var playAction: NotificationCompat.Action
    private var pauseAction: NotificationCompat.Action
    private var nextAction: NotificationCompat.Action
    private var prevAction: NotificationCompat.Action

    private var notificationManager: NotificationManager

    private var service: MusicService

    constructor(service: MusicService) {
        this.service = service

        notificationManager = service.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        playAction = NotificationCompat.Action(
            R.drawable.ic_play_arrow_white_24dp,
            service.getString(R.string.label_play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                service,
                PlaybackStateCompat.ACTION_PLAY
            )
        )
        pauseAction = NotificationCompat.Action(
            R.drawable.ic_pause_white_24dp,
            service.getString(R.string.label_pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                service,
                PlaybackStateCompat.ACTION_PAUSE
            )
        )
        nextAction = NotificationCompat.Action(
            R.drawable.ic_skip_next_white_24dp,
            service.getString(R.string.label_next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                service,
                PlaybackStateCompat.ACTION_SKIP_TO_NEXT
            )
        )
        prevAction = NotificationCompat.Action(
            R.drawable.ic_skip_previous_white_24dp,
            service.getString(R.string.label_previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(
                service,
                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
            )
        )

        notificationManager.cancelAll()
    }

    fun getNotificationManager(): NotificationManager = notificationManager

    fun getNotification(
        metadata: MediaMetadataCompat,
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token
    ): Notification {
        val isPlaying = state.state == PlaybackStateCompat.STATE_PLAYING
        val description: MediaDescriptionCompat = metadata.description
        val builder: NotificationCompat.Builder = buildNotification(state, token, isPlaying, description)
        return builder.build()
    }

    private fun buildNotification(
        state: PlaybackStateCompat,
        token: MediaSessionCompat.Token,
        isPlaying: Boolean,
        description: MediaDescriptionCompat
    ): NotificationCompat.Builder {
        if (isAndroidOOrHigher()) {
            createChannel()
        }

        val builder = NotificationCompat.Builder(service, CHANNEL_ID)
        builder.setStyle(MediaStyle().setMediaSession(token)
            .setShowActionsInCompactView(0, 1, 2)
            .setShowCancelButton(true)
            .setCancelButtonIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(service,
                PlaybackStateCompat.ACTION_STOP)))
            .setColor(ContextCompat.getColor(service, R.color.colorAccent))
            .setSmallIcon(R.drawable.ic_stat_image_audiotrack)
            .setContentIntent(createContentIntent())
            .setContentTitle(description.title)
            .setContentText(description.subtitle)
//            .setLargeIcon(MusicLibrary().getAlbumBitmap(service, description.mediaId!!))
            .setDeleteIntent(MediaButtonReceiver.buildMediaButtonPendingIntent(service,
                PlaybackStateCompat.ACTION_STOP))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS != 0L) {
            builder.addAction(prevAction)
        }

        builder.addAction(if (isPlaying) pauseAction else playAction)

        if (state.actions and PlaybackStateCompat.ACTION_SKIP_TO_NEXT != 0L) {
            builder.addAction(nextAction)
        }

        return builder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            // The user-visible name of the channel.
            val name = "MediaSession"
            // The user-visible description of the channel.
            val description = "MediaSession and MediaPlayer"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            // Configure the notification channel.
            mChannel.description = description
            mChannel.enableLights(true)
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.lightColor = Color.RED
            mChannel.enableVibration(true)
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            notificationManager.createNotificationChannel(mChannel)
            Log.d(TAG, "createChannel: New channel created")
        } else {
            Log.d(TAG, "createChannel: Existing channel reused")
        }
    }

    private fun isAndroidOOrHigher(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
    }

    private fun createContentIntent(): PendingIntent {
        val openUI = Intent(service, MainActivity::class.java)
        openUI.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getActivity(
            service, REQUEST_CODE, openUI, PendingIntent.FLAG_CANCEL_CURRENT
        )
    }

    companion object {
        const val NOTIFICATION_ID = 412

        private const val TAG = "NotificationManager"
        private const val CHANNEL_ID = "com.example.android.musicplayer.channel"
        private const val REQUEST_CODE = 501
    }
}