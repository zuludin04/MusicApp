package com.app.zuludin.musicapp.ui.main

interface MainPlayerListener {
    fun onPlayMusic(title: String)

    fun onMediaSelected(mediaId: String, position: Int)

    fun onPauseAndPlay()
}