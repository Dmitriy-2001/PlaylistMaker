package com.example.playlistmaker.player.domain.interfaces

interface AudioPlayerRepository {
    fun play()
    fun pause()
    fun release()
    fun currentPos(): Int
    fun prepare(previewUrl: String, callbackOnPrepared: () -> Unit, callbackOnCompletion: () -> Unit)
}