package com.app.zuludin.musicapp.data.source

import android.annotation.SuppressLint
import com.app.zuludin.musicapp.data.model.*
import com.app.zuludin.musicapp.util.MusicApiService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers

@SuppressLint("CheckResult")
class MusicRemoteImpl(private val apiService: MusicApiService) : MusicRemoteSource {

    override fun getTrendingList(callback: MusicRemoteSource.TrendingListCallback) {
        loadTrending()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onTrendingData(it)
                }
            }, {})
    }

    private fun loadTrending(): Single<TrendingData> {
        return Single.zip(
            apiService.getTrendingAlbums(),
            apiService.getTrendingSingles(),
            apiService.getTrendingSingles(),
            Function3<Albums, Singles, Singles, TrendingData> { albums, singles, _ ->
                TrendingData(albums, singles)
            }
        )
    }

    override fun getTrendingSingle(callback: MusicRemoteSource.TrendingSingleCallback) {
        apiService.getTrendingSingles()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onTrendingTrack(it)
                }
            }, {})
    }

    override fun getTrendingAlbum(callback: MusicRemoteSource.TrendingAlbumCallback) {
        apiService.getTrendingAlbums()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onTrendingAlbum(it)
                }
            }, {})
    }

    override fun getTopTrack(artistName: String, callback: MusicRemoteSource.TopTrackCallback) {
        apiService.getTopTrack(artistName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onTopTrack(it)
                }
            }, {})
    }

    override fun getArtistDetail(artistId: String, artistName: String, callback: MusicRemoteSource.DetailArtistCallback) {
        loadDetailArtist(artistId, artistName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onDetailArtist(it)
                }
            }, {})
    }

    private fun loadDetailArtist(artistId: String, artistName: String): Single<DetailArtist> {
        return Single.zip(
            apiService.getArtistById(artistId),
            apiService.getDetailAlbumByArtist(artistId),
            apiService.getTopTrack(artistName),
            Function3<ArtistData, AlbumData, TopTrack, DetailArtist> { artist, album, track ->
                DetailArtist(artist.artists?.get(0), album, track)
            }
        )
    }

    override fun getAlbumDetail(artistId: String, albumId: String, callback: MusicRemoteSource.DetailAlbumCallback) {
        loadDetailAlbum(artistId, albumId)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onDetailAlbum(it)
                }
            }, {})
    }

    private fun loadDetailAlbum(artistId: String, albumId: String): Single<DetailAlbum> {
        return Single.zip(
            apiService.getDetailAlbumByArtist(artistId),
            apiService.getTrackListByAlbum(albumId),
            apiService.getTrackListByAlbum(albumId),
            Function3<AlbumData, TrackAlbum, TrackAlbum, DetailAlbum> { album, track, _ ->
                DetailAlbum(album.album?.get(0), track.track)
            }
        )
    }

    override fun getTrackDetail(trackId: String, artistName: String, callback: MusicRemoteSource.DetailTrackCallback) {
        loadDetailTrack(trackId, artistName)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it != null) {
                    callback.onDetailTrack(it)
                }
            }, {})
    }

    private fun loadDetailTrack(trackId: String, artistName: String): Single<DetailTrack> {
        return Single.zip(
            apiService.getTrackSingle(trackId),
            apiService.getTopTrack(artistName),
            apiService.getTrackSingle(trackId),
            Function3<TrackData, TopTrack, TrackData, DetailTrack> { single, tracks, _ ->
                DetailTrack(single.track?.get(0), tracks.track)
            }
        )
    }
}