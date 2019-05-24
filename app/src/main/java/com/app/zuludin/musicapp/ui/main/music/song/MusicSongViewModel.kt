package com.app.zuludin.musicapp.ui.main.music.song

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.zuludin.musicapp.data.model.MusicList
import com.app.zuludin.musicapp.data.model.MusicMp3
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MusicSongViewModel {

    private lateinit var songListData: MutableLiveData<List<MusicMp3>>

    @SuppressLint("CheckResult")
    fun getData(resolver: ContentResolver): LiveData<List<MusicMp3>> {
        if (!::songListData.isInitialized) {
            songListData = MutableLiveData()
            loadSongList(resolver)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    songListData.value = it.list
                }, {})
        }
        return songListData
    }

    private fun addToMediaList(
        id: String,
        title: String,
        artist: String,
        album: String,
        duration: Long,
        path: String
    ): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, path)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .build()
    }

    private fun loadSongList(resolver: ContentResolver): Single<MusicList> {
        return Single.create {
            it.onSuccess(MusicList(loadMusicMp3(resolver)))
        }
    }

    private fun loadMusicMp3(resolver: ContentResolver): List<MusicMp3> {
        val selection = MediaStore.Audio.Media.IS_MUSIC
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM
        )

        val sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC"
        val musics: MutableList<MusicMp3> = mutableListOf()

        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            cursor = resolver.query(uri, projection, selection, null, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val title = cursor.getString(0)
                    val artist = cursor.getString(1)
                    val data = cursor.getString(2)
                    val albumId = cursor.getLong(3)
                    val duration = cursor.getLong(4)
                    val album = cursor.getString(5)
                    cursor.moveToNext()
                    if (data != null && data.endsWith(".mp3")) {
                        musics.add(MusicMp3(title, artist, data, albumId, duration, album))
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.toString())
        } finally {
            cursor?.close()
        }

        return musics
    }
}