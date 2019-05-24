package com.app.zuludin.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class TopTrack(

    @SerializedName("track")
    val track: List<TopTrackItem>? = null
)