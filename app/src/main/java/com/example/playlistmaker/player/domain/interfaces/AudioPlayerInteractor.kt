package com.example.playlistmaker.player.domain.interfaces

interface AudioPlayerInteractor {

    fun play()
    fun pause()
    fun release()
    fun getCurrentPos(): Int
    fun prepare(callbackPrep: () -> Unit, callbackComp: () -> Unit)

}