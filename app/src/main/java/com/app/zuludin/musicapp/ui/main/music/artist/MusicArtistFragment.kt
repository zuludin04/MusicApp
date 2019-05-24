package com.app.zuludin.musicapp.ui.main.music.artist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.app.zuludin.musicapp.ui.main.music.tracks.TrackListFragment
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.music_artist_fragment.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MusicArtistFragment : Fragment() {

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(MusicMp3::class, R.layout.item_music_artist) {
            MusicArtistViewHolder(it) {
                activity?.supportFragmentManager?.run {
                    beginTransaction()
                        .replace(R.id.frame_container, TrackListFragment.getInstance(it, false))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.music_artist_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = MusicArtistViewModel()
        view.recycler_artist.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = this@MusicArtistFragment.adapter
        }
        viewModel.getData(activity?.contentResolver!!).observe(this, Observer {
            adapter.items = it
        })
    }
}
