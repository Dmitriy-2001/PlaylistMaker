package com.example.playlistmaker.player.data.repository

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository
import java.util.Locale

class AudioPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer): AudioPlayerRepository {

    override fun setDataSource(url: String?) {
        mediaPlayer.setDataSource(url)
    }

    override fun preparePlayer() {
        mediaPlayer.prepareAsync()
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun currentPosition(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        mediaPlayer.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer.setOnCompletionListener(listener)
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }
}