package com.app.zuludin.musicapp.ui.album

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.AlbumsTrending
import com.app.zuludin.musicapp.data.model.TrackItem
import com.squareup.picasso.Picasso
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.album_detail_activity.*

class AlbumDetailActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(AlbumDetailViewModel::class.java)
    }

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(TrackItem::class, R.layout.item_album_track) { AlbumTrackViewHolder(it) }
    }

    private lateinit var albumData: AlbumsTrending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.album_detail_activity)
        setSupportActionBar(toolbar)
        albumData = intent.getParcelableExtra("AlbumData")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = albumData.strAlbum
        setupDetailLayout()
    }

    private fun setupDetailLayout() {
        detail_album_recycler.apply {
            layoutManager = LinearLayoutManager(this@AlbumDetailActivity)
            addItemDecoration(DividerItemDecoration(this@AlbumDetailActivity, DividerItemDecoration.VERTICAL))
            adapter = this@AlbumDetailActivity.adapter
        }

        viewModel.getAlbumDetail(albumData.idArtist.toString(), albumData.idAlbum.toString()).observe(this, Observer {
            Picasso.get().load(it.album?.strAlbumThumb).into(detail_album_thumbnail)
            detail_album_artist.text = it.album?.strArtist
            detail_album_description.text = it.album?.strDescriptionEN
            detail_album_released.text = it.album?.strReleaseFormat
            detail_album_genre.text = it.album?.strGenre
            detail_album_tracks.text = it.tracks?.size.toString()
            it.tracks?.let { tracks -> adapter.items = tracks }
        })
    }
}
