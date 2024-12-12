package com.example.playlistmaker.player.domain.interfaces

import android.media.MediaPlayer

interface AudioPlayerRepository {
    fun setDataSource(url: String?)
    fun preparePlayer()
    fun start()
    fun pause()
    fun currentPosition(): String
    fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener)
    fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener)
    fun release()
    fun isPlaying(): Boolean
}