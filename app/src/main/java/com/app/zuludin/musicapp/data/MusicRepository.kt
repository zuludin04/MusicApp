package com.app.zuludin.musicapp.data

import com.app.zuludin.musicapp.data.model.*
import com.app.zuludin.musicapp.data.source.MusicRemoteImpl
import com.app.zuludin.musicapp.data.source.MusicRemoteSource

class MusicRepository(private val remote: MusicRemoteImpl): MusicRemoteSource {
    override fun getTrendingSingle(callback: MusicRemoteSource.TrendingSingleCallback) {
        remote.getTrendingSingle(object : MusicRemoteSource.TrendingSingleCallback {
            override fun onTrendingTrack(tracks: Singles) {
                callback.onTrendingTrack(tracks)
            }
        })
    }

    override fun getTrendingAlbum(callback: MusicRemoteSource.TrendingAlbumCallback) {
        remote.getTrendingAlbum(object : MusicRemoteSource.TrendingAlbumCallback {
            override fun onTrendingAlbum(albums: Albums) {
                callback.onTrendingAlbum(albums)
            }
        })
    }

    override fun getTrendingList(callback: MusicRemoteSource.TrendingListCallback) {
        remote.getTrendingList(object : MusicRemoteSource.TrendingListCallback {
            override fun onTrendingData(trendingData: TrendingData) {
                callback.onTrendingData(trendingData)
            }
        })
    }

    override fun getTopTrack(artistName: String, callback: MusicRemoteSource.TopTrackCallback) {
        remote.getTopTrack(artistName, object : MusicRemoteSource.TopTrackCallback {
            override fun onTopTrack(topTrack: TopTrack) {
                callback.onTopTrack(topTrack)
            }
        })
    }

    override fun getArtistDetail(artistId: String, artistName: String, callback: MusicRemoteSource.DetailArtistCallback) {
        remote.getArtistDetail(artistId, artistName, object : MusicRemoteSource.DetailArtistCallback {
            override fun onDetailArtist(artist: DetailArtist) {
                callback.onDetailArtist(artist)
            }
        })
    }

    override fun getAlbumDetail(artistId: String, albumId: String, callback: MusicRemoteSource.DetailAlbumCallback) {
        remote.getAlbumDetail(artistId, albumId, object : MusicRemoteSource.DetailAlbumCallback {
            override fun onDetailAlbum(detailAlbum: DetailAlbum) {
                callback.onDetailAlbum(detailAlbum)
            }
        })
    }

    override fun getTrackDetail(trackId: String, artistName: String, callback: MusicRemoteSource.DetailTrackCallback) {
        remote.getTrackDetail(trackId, artistName, object : MusicRemoteSource.DetailTrackCallback {
            override fun onDetailTrack(track: DetailTrack) {
                callback.onDetailTrack(track)
            }
        })
    }
}