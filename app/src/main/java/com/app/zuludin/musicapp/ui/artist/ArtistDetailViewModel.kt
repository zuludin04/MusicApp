package com.app.zuludin.musicapp.ui.artist

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.zuludin.musicapp.data.MusicRepository
import com.app.zuludin.musicapp.data.model.DetailArtist
import com.app.zuludin.musicapp.data.source.MusicRemoteImpl
import com.app.zuludin.musicapp.data.source.MusicRemoteSource
import com.app.zuludin.musicapp.util.MusicApiClient

class ArtistDetailViewModel(application: Application): AndroidViewModel(application) {

    private val repository = MusicRepository(MusicRemoteImpl(MusicApiClient.create()))

    private lateinit var detailArtistData: MutableLiveData<DetailArtist>

    fun getDetail(artistId: String, artistName: String): LiveData<DetailArtist> {
        if (!::detailArtistData.isInitialized) {
            detailArtistData = MutableLiveData()
            loadArtistDetail(artistId, artistName)
        }
        return detailArtistData
    }

    private fun loadArtistDetail(artistId: String, artistName: String) {
        repository.getArtistDetail(artistId, artistName, object : MusicRemoteSource.DetailArtistCallback {
            override fun onDetailArtist(artist: DetailArtist) {
                detailArtistData.value = artist
            }
        })
    }
}