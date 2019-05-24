package com.app.zuludin.musicapp.util

import com.app.zuludin.musicapp.data.model.*
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicApiService {
    @GET("1/trending.php?country=us&type=itunes&format=albums")
    fun getTrendingAlbums(): Single<Albums>

    @GET("1/trending.php?country=us&type=itunes&format=singles")
    fun getTrendingSingles(): Single<Singles>

    @GET("1/track-top10.php")
    fun getTopTrack(@Query("s") artistName: String): Single<TopTrack>

    @GET("1/track.php")
    fun getTrackListByAlbum(@Query("m") albumId: String): Single<TrackAlbum>

    @GET("1/album.php")
    fun getDetailAlbumByArtist(@Query("i") artistId: String): Single<AlbumData>

    @GET("1/track.php")
    fun getTrackSingle(@Query("h") trackId: String): Single<TrackData>

    @GET("1/artist.php")
    fun getArtistById(@Query("i") artistId: String): Single<ArtistData>
}