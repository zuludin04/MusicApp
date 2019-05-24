package com.app.zuludin.musicapp.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AlbumsTrending(

    @SerializedName("strAlbumMBID")
    val strAlbumMBID: String? = null,

    @SerializedName("strArtistMBID")
    val strArtistMBID: String? = null,

    @SerializedName("idTrend")
    val idTrend: String? = null,

    @SerializedName("intChartPlace")
    val intChartPlace: String? = null,

    @SerializedName("strType")
    val strType: String? = null,

    @SerializedName("strArtist")
    val strArtist: String? = null,

    @SerializedName("strAlbumThumb")
    val strAlbumThumb: String? = null,

    @SerializedName("intWeek")
    val intWeek: String? = null,

    @SerializedName("idAlbum")
    val idAlbum: String? = null,

    @SerializedName("strTrack")
    val strTrack: String? = null,

    @SerializedName("dateAdded")
    val dateAdded: String? = null,

    @SerializedName("idArtist")
    val idArtist: String? = null,

    @SerializedName("strArtistThumb")
    val strArtistThumb: String? = null,

    @SerializedName("strCountry")
    val strCountry: String? = null,

    @SerializedName("idTrack")
    val idTrack: String? = null,

    @SerializedName("strAlbum")
    val strAlbum: String? = null,

    @SerializedName("strTrackMBID")
    val strTrackMBID: String? = null,

    @SerializedName("strTrackThumb")
    val strTrackThumb: String? = null
) : Parcelable