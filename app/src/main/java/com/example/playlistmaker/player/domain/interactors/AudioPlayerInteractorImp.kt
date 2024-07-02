package com.example.playlistmaker.player.domain.interactors

import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerTrack

class AudioPlayerInteractorImpl(
    private val playerTrack: PlayerTrack,
    private val audioPlayerRepository: AudioPlayerRepository
):

    AudioPlayerInteractor {

    override fun play() {
        audioPlayerRepository.play()
    }

    override fun pause() {
        audioPlayerRepository.pause()
    }

    override fun release() {
        audioPlayerRepository.release()
    }

    override fun getCurrentPos(): Int {
        return audioPlayerRepository.currentPos()
    }

    override fun prepare(
        callbackPrep: () -> Unit,
        callbackComp: () -> Unit) {
        audioPlayerRepository.prepare(
            previewUrl = playerTrack.previewUrl,
            callbackOnPrepared = {
                callbackPrep.invoke()
            },
            callbackOnCompletion = {
                callbackComp.invoke()
            }
        )
    }
}