package com.app.zuludin.musicapp.data.source

import com.app.zuludin.musicapp.data.model.*

interface MusicRemoteSource {

    // trending track, trending album, detail artist, detail album, detail track
    fun getTrendingSingle(callback: TrendingSingleCallback)

    fun getTrendingAlbum(callback: TrendingAlbumCallback)

    fun getTrendingList(callback: TrendingListCallback)

    fun getTopTrack(artistName: String, callback: TopTrackCallback)

    fun getArtistDetail(artistId: String, artistName: String, callback: DetailArtistCallback)

    fun getAlbumDetail(artistId: String, albumId: String, callback: DetailAlbumCallback)

    fun getTrackDetail(trackId: String, artistName: String, callback: DetailTrackCallback)

    interface TrendingListCallback {
        fun onTrendingData(trendingData: TrendingData)
    }

    interface TrendingSingleCallback {
        fun onTrendingTrack(tracks: Singles)
    }

    interface TrendingAlbumCallback {
        fun onTrendingAlbum(albums: Albums)
    }

    interface TopTrackCallback {
        fun onTopTrack(topTrack: TopTrack)
    }

    interface DetailArtistCallback {
        fun onDetailArtist(artist: DetailArtist)
    }

    interface DetailAlbumCallback {
        fun onDetailAlbum(detailAlbum: DetailAlbum)
    }

    interface DetailTrackCallback {
        fun onDetailTrack(track: DetailTrack)
    }
}