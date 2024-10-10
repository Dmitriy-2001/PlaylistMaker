package com.example.playlistmaker.player.domain.interfaces

import android.media.MediaPlayer

interface AudioPlayerInteractor {

    fun start()
    fun pause()
    fun release()
    fun preparePlayer()

    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun setDataSource(url: String?)
    fun isPlaying(): Boolean
}