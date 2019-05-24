package com.app.zuludin.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class AlbumData(
    @SerializedName("album")
    val album: List<AlbumItem>? = null
)