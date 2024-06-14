package com.example.playlistmaker.player.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.player.domain.models.PlayerTrack
import com.example.playlistmaker.utils.Creator

class PlayerViewModelFactory(private val playerTrack: PlayerTrack): ViewModelProvider.Factory {

    private val audioPlayerInteractor = Creator.provideAudioPlayerInteractor(playerTrack)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PlayerViewModel(playerTrack, audioPlayerInteractor) as T
    }
}