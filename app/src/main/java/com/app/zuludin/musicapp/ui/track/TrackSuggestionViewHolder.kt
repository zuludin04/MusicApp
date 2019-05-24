package com.app.zuludin.musicapp.ui.track

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.data.model.TopTrackItem
import com.squareup.picasso.Picasso
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import kotlinx.android.synthetic.main.item_album_track.view.*

class TrackSuggestionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView), SrvViewHolder<TopTrackItem> {
    override fun bind(item: TopTrackItem) {
        Picasso.get().load(item.strTrackThumb).into(itemView.album_track_thumbnail)
        itemView.album_track_title.text = item.strTrack
        itemView.album_track_duration.text = item.intDuration
    }
}