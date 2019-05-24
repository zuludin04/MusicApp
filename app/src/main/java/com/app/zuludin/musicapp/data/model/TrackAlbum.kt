package com.app.zuludin.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class TrackAlbum(
    @SerializedName("track")
    val track: List<TrackItem>? = null
)