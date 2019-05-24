package com.app.zuludin.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class TrackData(
    @SerializedName("track")
    val track: List<TrackSingle>? = null
)