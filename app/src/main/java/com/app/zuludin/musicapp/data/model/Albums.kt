package com.app.zuludin.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class Albums(

    @SerializedName("trending")
    val trending: List<AlbumsTrending>? = null
)