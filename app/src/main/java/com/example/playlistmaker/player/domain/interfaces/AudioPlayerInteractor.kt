package com.example.playlistmaker.player.domain.interfaces

import com.example.playlistmaker.player.domain.models.PlayerTrack

interface AudioPlayerInteractor {

    fun play()
    fun pause()
    fun release()
    fun getCurrentPos(): Int
    fun prepare(playerTrack: PlayerTrack, callbackPrep: () -> Unit, callbackComp: () -> Unit)

}