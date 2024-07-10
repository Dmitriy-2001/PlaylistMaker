package com.example.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository

class AudioPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer): AudioPlayerRepository {

    override fun play() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun currentPos(): Int {
        return mediaPlayer.currentPosition
    }

    override fun prepare(previewUrl: String, callbackOnPrepared: () -> Unit, callbackOnCompletion: () -> Unit) {
        mediaPlayer.setDataSource(previewUrl)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            callbackOnPrepared.invoke()
        }
        mediaPlayer.setOnCompletionListener {
            callbackOnCompletion.invoke()
        }
    }
}