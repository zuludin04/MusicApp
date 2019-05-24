package com.app.zuludin.musicapp.ui.main.discoveries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.Albums
import com.app.zuludin.musicapp.data.model.Singles
import com.tomasznajda.simplerecyclerview.adapter.AdvancedSrvAdapter
import kotlinx.android.synthetic.main.discoveries_fragment.view.*

class DiscoveriesFragment : Fragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(DiscoveriesViewModel::class.java)
    }

    private val adapter = AdvancedSrvAdapter().apply {
        addViewHolder(Albums::class, R.layout.item_recycler_more) { AlbumListViewHolder(it) }
        addViewHolder(Singles::class, R.layout.item_recycler_more) { TrackListViewHolder(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.discoveries_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter.set(ArrayList())

        view.recycler_discovery.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@DiscoveriesFragment.adapter
        }

        viewModel.getTrendingData().observe(this, Observer {
            it.albumTrending?.let { albums -> adapter.insert(albums) }
            it.singleTrending?.let { singles -> adapter.insert(singles) }
            view.progress_bar.visibility = View.GONE
        })
    }
}
