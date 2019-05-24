package com.app.zuludin.musicapp.ui.main.music.album

import android.graphics.BitmapFactory
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import kotlinx.android.synthetic.main.item_music_album.view.*

class MusicAlbumViewHolder(
    itemView: View,
    private val listener: (albumId: Long) -> Unit
) : RecyclerView.ViewHolder(itemView), SrvViewHolder<MusicMp3> {
    override fun bind(item: MusicMp3) {
        itemView.album_name.text = item.album
        itemView.album_tracks.text = item.title

        val bitmap = BitmapFactory.decodeFile(item.path)
        itemView.album_thumbnail.setImageBitmap(bitmap)

        itemView.setOnClickListener {
            listener(item.albumId)
        }
    }
}