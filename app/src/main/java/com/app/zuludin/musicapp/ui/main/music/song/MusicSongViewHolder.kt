package com.app.zuludin.musicapp.ui.main.music.song

import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import kotlinx.android.synthetic.main.item_music_song.view.*

class MusicSongViewHolder(
    itemView: View,
    private val listener: (mediaId: String, position: Int) -> Unit
) : RecyclerView.ViewHolder(itemView), SrvViewHolder<MusicMp3> {
    override fun bind(item: MusicMp3) {
        itemView.music_title.text = item.title

        val minutes = (item.duration / 1000) / 60
        val seconds = (item.duration / 1000) % 60
        val duration = "${minutes}m ${seconds}s"
        itemView.music_duration.text = duration

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(item.path)
        val data: ByteArray? = mmr.embeddedPicture
        data?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            itemView.music_thumbnail.setImageBitmap(bitmap)
        }

        itemView.setOnClickListener { listener(item.albumId.toString(), adapterPosition) }
    }
}