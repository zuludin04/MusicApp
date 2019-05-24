package com.app.zuludin.musicapp.ui.artist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.AlbumData
import com.app.zuludin.musicapp.data.model.SinglesTrending
import com.app.zuludin.musicapp.data.model.TopTrack
import com.squareup.picasso.Picasso
import com.tomasznajda.simplerecyclerview.adapter.AdvancedSrvAdapter
import kotlinx.android.synthetic.main.artist_detail_activity.*

class ArtistDetailActivity : AppCompatActivity() {

    private val adapter = AdvancedSrvAdapter().apply {
        addViewHolder(AlbumData::class, R.layout.item_recycler_more) { AlbumListViewHolder(it) }
        addViewHolder(TopTrack::class, R.layout.item_recycler_more) { TrackListViewHolder(it) }
    }

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(ArtistDetailViewModel::class.java)
    }

    private lateinit var singleData: SinglesTrending

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.artist_detail_activity)
        setSupportActionBar(toolbar)
        singleData = intent.getParcelableExtra("SingleData")
        supportActionBar?.apply {
            title = singleData.strArtist
            setDisplayHomeAsUpEnabled(true)
        }

        setupDetailLayout()
    }

    private fun setupDetailLayout() {
        adapter.set(ArrayList())

        val artistId = intent.getStringExtra("ArtistId")
        val artistName = intent.getStringExtra("ArtistName")

        detail_artist_recycler.apply {
            layoutManager = LinearLayoutManager(this@ArtistDetailActivity)
            adapter = this@ArtistDetailActivity.adapter
        }

        viewModel.getDetail(singleData.idArtist.toString(), singleData.strArtist.toString()).observe(this, Observer {
            Picasso.get().load(it.artist?.strArtistThumb).into(detail_artist_thumbnail)
            detail_artist_formed.text = it.artist?.intFormedYear
            detail_artist_genre.text = it.artist?.strGenre
            detail_artist_description.text = it.artist?.strBiographyEN

            it.albumList?.let { albums -> adapter.insert(albums) }
            it.trackList?.let { tracks -> adapter.insert(tracks) }
        })
    }
}
