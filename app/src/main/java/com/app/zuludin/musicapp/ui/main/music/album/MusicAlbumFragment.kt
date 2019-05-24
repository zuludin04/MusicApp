package com.app.zuludin.musicapp.ui.main.music.album

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.app.zuludin.musicapp.ui.main.music.tracks.TrackListFragment
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.music_album_fragment.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MusicAlbumFragment : Fragment() {

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(MusicMp3::class, R.layout.item_music_album) {
            MusicAlbumViewHolder(it) {
                activity?.supportFragmentManager?.run {
                    beginTransaction()
                        .replace(R.id.frame_container, TrackListFragment.getInstance(it, true))
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
        return inflater.inflate(R.layout.music_album_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = MusicAlbumViewModel()
        view.recycler_album.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = this@MusicAlbumFragment.adapter
        }
        viewModel.getData(activity?.contentResolver!!).observe(this, Observer {
            adapter.items = it
        })
    }
}
