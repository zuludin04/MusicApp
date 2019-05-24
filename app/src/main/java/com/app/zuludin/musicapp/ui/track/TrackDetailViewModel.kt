package com.app.zuludin.musicapp.ui.track

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.zuludin.musicapp.data.MusicRepository
import com.app.zuludin.musicapp.data.model.DetailTrack
import com.app.zuludin.musicapp.data.model.TrackAlbum
import com.app.zuludin.musicapp.data.model.TrackItem
import com.app.zuludin.musicapp.data.source.MusicRemoteImpl
import com.app.zuludin.musicapp.data.source.MusicRemoteSource
import com.app.zuludin.musicapp.util.MusicApiClient

class TrackDetailViewModel(application: Application): AndroidViewModel(application) {

    private val repository = MusicRepository(MusicRemoteImpl(MusicApiClient.create()))

    private lateinit var detailTrackData: MutableLiveData<DetailTrack>

    fun getTrackData(albumId: String, artistName: String): LiveData<DetailTrack> {
        if (!::detailTrackData.isInitialized) {
            detailTrackData = MutableLiveData()
            loadTrackDetail(albumId, artistName)
        }
        return detailTrackData
    }

    private fun loadTrackDetail(albumId: String, artistName: String) {
        repository.getTrackDetail(albumId, artistName, object : MusicRemoteSource.DetailTrackCallback {
            override fun onDetailTrack(track: DetailTrack) {
                detailTrackData.value = track
            }
        })
    }
}