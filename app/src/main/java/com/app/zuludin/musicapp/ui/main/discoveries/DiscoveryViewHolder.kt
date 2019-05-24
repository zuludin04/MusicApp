package com.app.zuludin.musicapp.ui.main.discoveries

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.Albums
import com.app.zuludin.musicapp.data.model.AlbumsTrending
import com.app.zuludin.musicapp.data.model.Singles
import com.app.zuludin.musicapp.data.model.SinglesTrending
import com.app.zuludin.musicapp.ui.album.AlbumDetailActivity
import com.app.zuludin.musicapp.ui.track.TrackDetailActivity
import com.squareup.picasso.Picasso
import com.tomasznajda.simplerecyclerview.SrvViewHolder
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.item_album_trending.view.*
import kotlinx.android.synthetic.main.item_recycler_more.view.*
import kotlinx.android.synthetic.main.item_track_trending.view.*

class AlbumListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<Albums> {
    private val adapter by lazy {
        BasicSrvAdapter().apply {
            addViewHolder(AlbumsTrending::class, R.layout.item_album_trending) { AlbumViewHolder(it) }
        }
    }

    override fun bind(item: Albums) {
        itemView.header_text.text = itemView.context.getString(R.string.album)
        itemView.recycler_more.apply {
            layoutManager = GridLayoutManager(itemView.context, 2)
            adapter = this@AlbumListViewHolder.adapter
        }
        item.trending?.let {
            adapter.items = it
        }
    }
}

class TrackListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<Singles> {
    private val adapter by lazy {
        BasicSrvAdapter().apply {
            addViewHolder(SinglesTrending::class, R.layout.item_track_trending) { TrackViewHolder(it) }
        }
    }

    override fun bind(item: Singles) {
        itemView.header_text.text = "Singles"
        itemView.recycler_more.apply {
            layoutManager = GridLayoutManager(itemView.context, 2)
            adapter = this@TrackListViewHolder.adapter
        }
        item.trending?.let {
            adapter.items = it
        }
    }
}

class AlbumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<AlbumsTrending> {
    override fun bind(item: AlbumsTrending) {
        itemView.album_artist.text = item.strArtist
        Picasso.get().load(item.strAlbumThumb).into(itemView.album_thumbnail)

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, AlbumDetailActivity::class.java)
            intent.putExtra("AlbumData", item)
            itemView.context.startActivity(intent)
        }
    }
}

class TrackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), SrvViewHolder<SinglesTrending> {
    override fun bind(item: SinglesTrending) {
        Picasso.get().load(item.strTrackThumb).into(itemView.track_thumbnail)
        itemView.track_artist.text = item.strArtist
        itemView.track_album.text = item.strAlbum

        itemView.setOnClickListener {
            val intent = Intent(itemView.context, TrackDetailActivity::class.java)
            intent.putExtra("TrackData", item)
            itemView.context.startActivity(intent)
        }
    }
}