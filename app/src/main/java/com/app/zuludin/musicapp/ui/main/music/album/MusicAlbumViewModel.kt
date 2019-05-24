package com.app.zuludin.musicapp.ui.main.music.album

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.zuludin.musicapp.data.model.MusicList
import com.app.zuludin.musicapp.data.model.MusicMp3
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MusicAlbumViewModel {

    private lateinit var albumData: MutableLiveData<List<MusicMp3>>

    @SuppressLint("CheckResult")
    fun getData(resolver: ContentResolver): LiveData<List<MusicMp3>> {
        if (!::albumData.isInitialized) {
            albumData = MutableLiveData()
            loadAlbumData(resolver)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    it?.let {musics ->
                        if (musics.list.isNotEmpty()) {
                            albumData.value = musics.list
                        }
                    }
                }, {})
        }
        return albumData
    }

    private fun loadAlbumData(resolver: ContentResolver): Single<MusicList> {
        return Single.create { it.onSuccess(MusicList(getAlbum(resolver))) }
    }

    private fun getAlbum(resolver: ContentResolver): List<MusicMp3> {
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST,
            MediaStore.Audio.Albums.ALBUM_ART,
            MediaStore.Audio.Albums.NUMBER_OF_SONGS
        )

        val sortOrder = MediaStore.Audio.Media.ALBUM + " COLLATE LOCALIZED ASC"
        val musics: MutableList<MusicMp3> = mutableListOf()

        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
            cursor = resolver.query(uri, projection, null, null, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val id = cursor.getLong(0)
                    val album = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val albumArt = cursor.getString(3)
                    val numberOfSongs = cursor.getString(4)
                    cursor.moveToNext()
                    musics.add(MusicMp3(numberOfSongs, artist, albumArt, id, 0, album))
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