package com.example.playlistmaker.player.domain.interactors

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository

class AudioPlayerInteractorImpl(
    private val audioPlayerRepository: AudioPlayerRepository
):

    AudioPlayerInteractor {
    override fun setDataSource(url: String?) {
        audioPlayerRepository.setDataSource(url)
    }

    override fun preparePlayer() {
        audioPlayerRepository.preparePlayer()
    }

    override fun start() {
        audioPlayerRepository.start()
    }

    override fun pause() {
        audioPlayerRepository.pause()
    }

    override fun currentPosition(): String {
        return audioPlayerRepository.currentPosition()
    }

    override fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        audioPlayerRepository.setOnPreparedListener(listener)
    }

    override fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        audioPlayerRepository.setOnCompletionListener(listener)
    }

    override fun release() {
        audioPlayerRepository.release()
    }

    override fun isPlaying(): Boolean {
        return audioPlayerRepository.isPlaying()
    }
}