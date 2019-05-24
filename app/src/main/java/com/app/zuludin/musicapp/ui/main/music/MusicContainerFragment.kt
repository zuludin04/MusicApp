package com.app.zuludin.musicapp.ui.main.music

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.app.zuludin.musicapp.R
import kotlinx.android.synthetic.main.music_container_fragment.view.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MusicContainerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.music_container_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.music_pager.adapter = TabAdapter(childFragmentManager)
        view.music_pager.offscreenPageLimit = 2
        view.tabs.setupWithViewPager(view.music_pager)
    }
}
