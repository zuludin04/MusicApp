package com.app.zuludin.musicapp.ui.artist

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.AlbumData
import com.app.zuludin.musicapp.data.model.AlbumItem
import com.app.zuludin.musicapp.data.model.TopTrack
import com.app.zuludin.musicapp.data.model.TopTrackItem
import com.squareup.picasso.Picasso
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.item_album_trending.view.*
import kotlinx.android.synthetic.main.item_recycler_more.view.*
import kotlinx.android.synthetic.main.item_track_trending.view.*

class AlbumListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<AlbumData> {
    private val adapter by lazy {
        BasicSrvAdapter().apply {
            addViewHolder(AlbumItem::class, R.layout.item_album_trending) { ArtistAlbumViewHolder(it) }
        }
    }

    override fun bind(item: AlbumData) {
        itemView.header_text.text = "Albums"
        itemView.recycler_more.apply {
            layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@AlbumListViewHolder.adapter
        }
        item.album?.let { adapter.items = it }
    }
}

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<TopTrack> {
    private val adapter by lazy {
        BasicSrvAdapter().apply {
            addViewHolder(TopTrackItem::class, R.layout.item_track_trending) { ArtistTrackViewHolder(it) }
        }
    }

    override fun bind(item: TopTrack) {
        itemView.header_text.text = "Top Tracks"
        itemView.recycler_more.apply {
            layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@TrackListViewHolder.adapter
        }
        item.track?.let { adapter.items = it }
    }
}

class ArtistAlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<AlbumItem> {
    override fun bind(item: AlbumItem) {
        Picasso.get().load(item.strAlbumThumb).into(itemView.album_thumbnail)
        itemView.album_artist.text = item.strArtist
    }
}

class ArtistTrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<TopTrackItem> {
    override fun bind(item: TopTrackItem) {
        Picasso.get().load(item.strTrackThumb).into(itemView.track_thumbnail)
        itemView.track_artist.text = item.strArtist
        itemView.track_album.text = item.strAlbum
    }
}