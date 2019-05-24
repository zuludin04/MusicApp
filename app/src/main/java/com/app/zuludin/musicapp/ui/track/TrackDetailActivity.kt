package com.app.zuludin.musicapp.ui.track

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.SinglesTrending
import com.app.zuludin.musicapp.data.model.TopTrackItem
import com.app.zuludin.musicapp.ui.artist.ArtistDetailActivity
import com.squareup.picasso.Picasso
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.track_detail_activity.*

class TrackDetailActivity : AppCompatActivity() {

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(TopTrackItem::class, R.layout.item_album_track) { TrackSuggestionViewHolder(it) }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(TrackDetailViewModel::class.java)
    }

    private lateinit var singleData: SinglesTrending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_detail_activity)
        setSupportActionBar(toolbar)
        singleData = intent.getParcelableExtra("TrackData")
        supportActionBar?.apply {
            title = singleData.strTrack
            setDisplayHomeAsUpEnabled(true)
        }
        setupDetailLayout()
    }

    private fun setupDetailLayout() {
        detail_track_recycler.apply {
            layoutManager = LinearLayoutManager(this@TrackDetailActivity)
            addItemDecoration(DividerItemDecoration(this@TrackDetailActivity, DividerItemDecoration.VERTICAL))
            adapter = this@TrackDetailActivity.adapter
        }

        viewModel.getTrackData(singleData.idTrack.toString(), singleData.strArtist.toString()).observe(this, Observer {
            Picasso.get().load(it.track?.strTrackThumb).into(detail_track_thumbnail)
            detail_track_artist.text = it.track?.strArtist
            detail_track_number.text = it.track?.intTrackNumber
            detail_track_album.text = it.track?.strAlbum
            detail_track_genre.text = it.track?.strGenre

            it.suggestions?.let { tracks -> adapter.items = tracks }
        })

        more_artist.setOnClickListener {
            val intent = Intent(this, ArtistDetailActivity::class.java)
            intent.putExtra("SingleData", singleData)
            startActivity(intent)
        }
    }
}
