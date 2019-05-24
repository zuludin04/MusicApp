package com.app.zuludin.musicapp.ui.main.music

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.app.zuludin.musicapp.ui.main.music.album.MusicAlbumFragment
import com.app.zuludin.musicapp.ui.main.music.artist.MusicArtistFragment
import com.app.zuludin.musicapp.ui.main.music.song.MusicSongFragment

class TabAdapter(manager: FragmentManager): FragmentPagerAdapter(manager) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> MusicAlbumFragment()
            1 -> MusicArtistFragment()
            2 -> MusicSongFragment()
            else -> MusicAlbumFragment()
        }
    }

    override fun getCount(): Int = 3

    override fun getPageTitle(position: Int): CharSequence? {
        val titles: Array<String> = arrayOf("Album", "Artist", "Song")
        return titles[position]
    }
}