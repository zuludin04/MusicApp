package com.app.zuludin.musicapp.ui.main.music.tracks

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.MyApplication
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.data.model.MusicMp3
import com.app.zuludin.musicapp.ui.main.MainPlayerListener
import com.app.zuludin.musicapp.ui.main.music.song.MusicSongViewHolder
import com.tomasznajda.simplerecyclerview.adapter.BasicSrvAdapter
import kotlinx.android.synthetic.main.track_list_fragment.view.*
import java.util.*

/**
 * A simple [Fragment] subclass.
 *
 */
class TrackListFragment : Fragment() {

    private val adapter = BasicSrvAdapter().apply {
        addViewHolder(MusicMp3::class, R.layout.item_music_song) {
            MusicSongViewHolder(it) { id, position ->
                playerListener.onMediaSelected(id, position)
            }
        }
    }

    private lateinit var songList: ArrayList<MusicMp3>
    private lateinit var playerListener: MainPlayerListener
    private lateinit var myApplication: MyApplication

    override fun onAttach(context: Context) {
        super.onAttach(context)
        playerListener = activity as MainPlayerListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.track_list_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        myApplication = MyApplication.getInstance()

        val isAlbum = arguments?.getBoolean("IS_ALBUM")
        val dataId = arguments?.getLong("DATA_ID")

        songList = if (isAlbum!!) {
            detailSongByAlbumId(requireActivity().contentResolver, dataId!!)
        } else {
            detailSongByArtistId(requireActivity().contentResolver, dataId!!)
        }

        insertSongIntoMetadata(songList)

        view.recycler_song.apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            adapter = this@TrackListFragment.adapter
        }

        adapter.items = songList
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

    private fun detailSongByAlbumId(resolver: ContentResolver, albumId: Long): ArrayList<MusicMp3> {
        var selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        if (albumId > 0) {
            selection = "$selection and album_id = $albumId"
        }

        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC"
        val musics: ArrayList<MusicMp3> = ArrayList()

        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            cursor = resolver.query(uri, projection, selection, null, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val album = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val path = cursor.getString(5)
                    cursor.moveToNext()
                    musics.add(MusicMp3(title, artist, path, id.toLong(), duration, album))
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.toString())
        } finally {
            cursor?.close()
        }

        return musics
    }

    private fun detailSongByArtistId(resolver: ContentResolver, artistId: Long): ArrayList<MusicMp3> {
        var selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        if (artistId > 0) {
            selection = "$selection and artist_id = $artistId"
        }

        val projection: Array<String> = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )

        val sortOrder = MediaStore.Audio.Media.TITLE + " COLLATE LOCALIZED ASC"
        val musics: ArrayList<MusicMp3> = ArrayList()

        var cursor: Cursor? = null
        try {
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            cursor = resolver.query(uri, projection, selection, null, sortOrder)
            if (cursor != null) {
                cursor.moveToFirst()
                while (!cursor.isAfterLast) {
                    val id = cursor.getString(0)
                    val title = cursor.getString(1)
                    val artist = cursor.getString(2)
                    val album = cursor.getString(3)
                    val duration = cursor.getLong(4)
                    val path = cursor.getString(5)
                    cursor.moveToNext()
                    musics.add(MusicMp3(title, artist, path, id.toLong(), duration, album))
                }
            }
        } catch (e: Exception) {
            Log.e("error", e.toString())
        } finally {
            cursor?.close()
        }

        return musics
    }

    companion object {
        fun getInstance(dataId: Long, isAlbum: Boolean): TrackListFragment {
            val fragment = TrackListFragment()
            val args = Bundle()
            args.putLong("DATA_ID", dataId)
            args.putBoolean("IS_ALBUM", isAlbum)
            fragment.arguments = args
            return fragment
        }
    }
}
