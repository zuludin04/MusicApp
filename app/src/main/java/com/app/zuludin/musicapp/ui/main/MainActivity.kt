package com.app.zuludin.musicapp.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.view.Gravity
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.zuludin.musicapp.R
import com.app.zuludin.musicapp.ui.main.discoveries.DiscoveriesFragment
import com.app.zuludin.musicapp.ui.main.music.MusicContainerFragment
import com.app.zuludin.musicapp.util.client.MediaBrowserHelper
import com.app.zuludin.musicapp.util.client.MediaBrowserHelperCallback
import com.app.zuludin.musicapp.util.service.MusicService
import kotlinx.android.synthetic.main.main_activity.*
import kotlinx.android.synthetic.main.main_content.*
import kotlinx.android.synthetic.main.main_drawer_layout.*
import kotlinx.android.synthetic.main.media_controller_layout.*

class MainActivity : AppCompatActivity(), MainPlayerListener, MediaBrowserHelperCallback {

    private lateinit var mediaBrowserHelper: MediaBrowserHelper
    private var isMusicPlay = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        setSupportActionBar(toolbar)

        if (!permissionGranted()) {
            requestPermission()
        }

        mediaBrowserHelper = MediaBrowserHelper(this, MusicService::class.java)
        mediaBrowserHelper.setMediaBrowserHelperCallback(this)

        setupDrawerMenu()
        setupBottomController()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.frame_container, DiscoveriesFragment())
                .commit()
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowserHelper.onStart(false)
    }

    override fun onStop() {
        super.onStop()
        mediaBrowserHelper.onStop()
    }

    override fun onMetadataChanged(metadata: MediaMetadataCompat) {
        bottom_music_title.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE)
        bottom_music_artist.text = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST)
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(metadata.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI))
        val data: ByteArray? = mmr.embeddedPicture
        data?.let {
            val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
            bottom_music_thumbnail.setImageBitmap(bitmap)
            sheet_music_thumbnail.setImageBitmap(bitmap)
        }
    }

    override fun onPlaybackStateChanged(state: PlaybackStateCompat) {
        isMusicPlay = state != null && state.state == PlaybackStateCompat.STATE_PLAYING
//        getControllerFragment().isMediaPlaying(isMusicPlay)
    }

    override fun onMediaControllerConnected(mediaController: MediaControllerCompat) {

    }

    override fun onPlayMusic(title: String) {

    }

    override fun onPauseAndPlay() {
        if (isMusicPlay) {
            mediaBrowserHelper.getTransportControls().pause()
            bottom_play_pause_button.setImageResource(R.drawable.ic_pause_circle_outline_white_24dp)
            sheet_play_pause_button.setImageResource(R.drawable.ic_pause_white_24dp)
        } else {
            mediaBrowserHelper.getTransportControls().play()
            bottom_play_pause_button.setImageResource(R.drawable.ic_play_circle_outline_white_24dp)
            sheet_play_pause_button.setImageResource(R.drawable.ic_play_arrow_white_24dp)
        }
    }

    override fun onMediaSelected(mediaId: String, position: Int) {
        val bundle = Bundle()
        bundle.putInt("SONG_POSITION", position)
        mediaBrowserHelper.subscribeToNewPlaylist("phone_default", "12")
        mediaBrowserHelper.getTransportControls().playFromMediaId(mediaId, bundle)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_container, fragment)
            .commit()

        drawer_layout.closeDrawer(Gravity.LEFT)
    }

    private fun permissionGranted(): Boolean {
        val storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        return storagePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        val permissions: Array<String> = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        ActivityCompat.requestPermissions(this, permissions, 4)
    }

    private fun setupBottomController() {
        bottom_play_pause_button.setOnClickListener { onPauseAndPlay() }
        sheet_next_button.setOnClickListener { mediaBrowserHelper.getTransportControls().skipToNext() }
        sheet_prev_button.setOnClickListener { mediaBrowserHelper.getTransportControls().skipToPrevious() }
        sheet_play_pause_button.setOnClickListener { onPauseAndPlay() }
    }

    private fun setupDrawerMenu() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        menu_list.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            adapter = DrawerMenuAdapter(menuList()) { position ->
                when (position) {
                    0 -> replaceFragment(DiscoveriesFragment())
                    1 -> replaceFragment(MusicContainerFragment())
                }
            }
        }

        drawer_layout.setViewScale(Gravity.START, 0.9f)
        drawer_layout.setViewElevation(Gravity.START, 20f)
    }

    private fun menuList(): List<DrawerMenu> {
        val list: MutableList<DrawerMenu> = mutableListOf()

        val titles: Array<String> = arrayOf(
            "Discovery", "Music", "Settings"
        )

        val icons: Array<Int> = arrayOf(
            R.drawable.ic_discoveries, R.drawable.ic_music, R.drawable.ic_settings
        )

        for (i in titles.indices) {
            list.add(DrawerMenu(titles[i], icons[i]))
        }

        return list
    }
}
