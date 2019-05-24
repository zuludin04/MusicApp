package com.app.zuludin.musicapp.ui.main.discoveries

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.zuludin.musicapp.data.MusicRepository
import com.app.zuludin.musicapp.data.model.TrendingData
import com.app.zuludin.musicapp.data.source.MusicRemoteImpl
import com.app.zuludin.musicapp.data.source.MusicRemoteSource
import com.app.zuludin.musicapp.util.MusicApiClient

class DiscoveriesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MusicRepository(MusicRemoteImpl(MusicApiClient.create()))

    private lateinit var trendingListData: MutableLiveData<TrendingData>

    fun getTrendingData(): LiveData<TrendingData> {
        if (!::trendingListData.isInitialized) {
            trendingListData = MutableLiveData()
            loadTrending()
        }
        return trendingListData
    }

    private fun loadTrending() {
        repository.getTrendingList(object : MusicRemoteSource.TrendingListCallback {
            override fun onTrendingData(trendingData: TrendingData) {
                trendingListData.value = trendingData
            }
        })
    }
}