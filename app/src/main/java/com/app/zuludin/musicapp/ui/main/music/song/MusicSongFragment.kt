package com.app.zuludin.musicapp.ui.main.music.song

import android.content.Context
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.MyApplication
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.app.zuludin.musicapp.ui.main.MainPlayerListener
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.music_song_fragment.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class MusicSongFragment : Fragment() {

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(MusicMp3::class, R.layout.item_music_song) {
            MusicSongViewHolder(it) { id, position ->
                insertSongIntoMetadata(songList)
                playerListener.onMediaSelected(id, position)
                Toast.makeText(requireContext(), "$position", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private lateinit var playerListener: MainPlayerListener
    private lateinit var myApplication: MyApplication
    private var songList: ArrayList<MusicMp3> = ArrayList()

    override fun onAttach(context: Context) {
        super.onAttach(context)
        playerListener = activity as MainPlayerListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.music_song_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = MusicSongViewModel()

        myApplication = MyApplication.getInstance()

        view.recycler_music.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = this@MusicSongFragment.adapter
        }
        viewModel.getData(activity?.contentResolver!!).observe(this, Observer { musics ->
            adapter.items = musics
            songList.addAll(musics)
        })
    }

    private fun insertSongIntoMetadata(songs: ArrayList<MusicMp3>) {
        val list: MutableList<MediaMetadataCompat> = mutableListOf()
        for (music in songs) {
            list.add(
                addToMediaList(
                    music.albumId.toString(),
                    music.title,
                    music.artist,
                    music.album,
                    music.duration,
                    music.path
                )
            )
        }
        myApplication.setMediaItems(list)
    }

    private fun addToMediaList(
        id: String,
        title: String,
        artist: String,
        album: String,
        duration: Long,
        path: String
    ): MediaMetadataCompat {
        return MediaMetadataCompat.Builder()
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
            .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, artist)
            .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
            .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, path)
            .putString(MediaMetadataCompat.METADATA_KEY_ALBUM, album)
            .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
            .build()
    }
}
