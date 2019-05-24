package com.app.zuludin.musicapp.ui.main.music.artist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import kotlinx.android.synthetic.main.item_music_artist.view.*

class MusicArtistViewHolder(
    itemView: View,
    private val listener: (albumId: Long) -> Unit
) : RecyclerView.ViewHolder(itemView), SrvViewHolder<MusicMp3> {
    override fun bind(item: MusicMp3) {
        itemView.artist_name.text = item.artist
        itemView.artist_tracks.text = item.album
        val logo = item.artist.substring(range = 0..1)
        itemView.artist_logo.text = logo

        itemView.setOnClickListener {
            listener(item.albumId)
        }
    }
}