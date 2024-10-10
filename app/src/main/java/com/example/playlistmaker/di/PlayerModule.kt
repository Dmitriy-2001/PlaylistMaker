package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    factory { MediaPlayer() }

    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(mediaPlayer = get())
    }

    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(audioPlayerRepository = get())
    }

    viewModel { (track: Track) ->
        PlayerViewModel(track = track, audioPlayerInteractor = get())
    }

}