package com.app.zuludin.musicapp.data.model

import android.os.Parcel
import android.os.Parcelable

data class MusicMp3(
    val title: String,
    val artist: String,
    val path: String,
    val albumId: Long,
    val duration: Long,
    val album: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(artist)
        parcel.writeString(path)
        parcel.writeLong(albumId)
        parcel.writeLong(duration)
        parcel.writeString(album)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MusicMp3> {
        override fun createFromParcel(parcel: Parcel): MusicMp3 {
            return MusicMp3(parcel)
        }

        override fun newArray(size: Int): Array<MusicMp3?> {
            return arrayOfNulls(size)
        }
    }
}

data class MusicList(
    val list: List<MusicMp3>
)