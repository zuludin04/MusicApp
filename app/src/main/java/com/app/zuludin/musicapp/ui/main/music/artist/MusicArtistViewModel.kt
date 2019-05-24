package com.app.zuludin.musicapp.ui.main.music.artist

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

class MusicArtistViewModel {

    private lateinit var artistListData: MutableLiveData<List<MusicMp3>>

    @SuppressLint("CheckResult")
    fun getData(resolver: ContentResolver): LiveData<List<MusicMp3>> {
        if (!::artistListData.isInitialized) {
            artistListData = MutableLiveData()
            loadArtistList(resolver)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    artistListData.value = it.list
                }, {})
        }
        return artistListData
    }

    private fun loadArtistList(resolver: ContentResolver): Single<MusicList> {
        return Single.create {
            it.onSuccess(MusicList(getArtist(resolver)))
        }
    }

    private fun getArtist(resolver: ContentResolver): List<MusicMp3> {
        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Artists._ID,
            MediaStore.Audio.Artists.ARTIST,
            MediaStore.Audio.Artists.NUMBER_OF_TRACKS
        )

        val musics: MutableList<MusicMp3> = mutableListOf()

        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI
            cursor = resolver.query(uri, projection, null, null, null)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val id = cursor.getLong(0)
                    val artist = cursor.getString(1)
                    val numberOfTracks = cursor.getString(2)
                    cursor.moveToNext()
                    musics.add(MusicMp3("", artist, "", id, 0, numberOfTracks))
                }
            }
        } catch (e: Exception) {
            Log.e("errorArtist", e.toString())
        } finally {
            cursor?.close()
        }

        return musics
    }
}