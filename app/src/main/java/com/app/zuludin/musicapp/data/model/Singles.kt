package com.app.zuludin.musicapp.data.model

import com.google.gson.annotations.SerializedName

data class Singles(

	@SerializedName("trending")
	val trending: List<SinglesTrending>? = null
)