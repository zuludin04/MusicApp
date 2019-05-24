package com.app.zuludin.musicapp.ui.album

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.zuludin.musicapp.data.MusicRepository
import com.app.zuludin.musicapp.data.model.DetailAlbum
import com.app.zuludin.musicapp.data.source.MusicRemoteImpl
import com.app.zuludin.musicapp.data.source.MusicRemoteSource
import com.app.zuludin.musicapp.util.MusicApiClient

class AlbumDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(MusicRemoteImpl(MusicApiClient.create()))

    private lateinit var detailAlbumData: MutableLiveData<DetailAlbum>

    fun getAlbumDetail(artistId: String, albumId: String): LiveData<DetailAlbum> {
        if (!::detailAlbumData.isInitialized) {
            detailAlbumData = MutableLiveData()
            loadAlbum(artistId, albumId)
        }
        return detailAlbumData
    }

    private fun loadAlbum(artistId: String, albumId: String) {
        repository.getAlbumDetail(artistId, albumId, object : MusicRemoteSource.DetailAlbumCallback {
            override fun onDetailAlbum(detailAlbum: DetailAlbum) {
                detailAlbumData.value = detailAlbum
            }
        })
    }
}