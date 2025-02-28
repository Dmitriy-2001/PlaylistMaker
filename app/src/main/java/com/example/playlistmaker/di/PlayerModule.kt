package com.example.playlistmaker.di

import android.media.MediaPlayer
import com.example.playlistmaker.media.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository
import com.example.playlistmaker.player.presentation.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val playerModule = module {
    // Медиа-плеер
    factory { MediaPlayer() }

    // Репозиторий аудиоплеера
    factory<AudioPlayerRepository> {
        AudioPlayerRepositoryImpl(mediaPlayer = get())
    }

    // Интерактор аудиоплеера
    factory<AudioPlayerInteractor> {
        AudioPlayerInteractorImpl(audioPlayerRepository = get())
    }

    // Используем уже созданные интеракторы из `medialibraryModule`
    single { get<FavoriteTracksInteractor>() }
    single { get<PlaylistsInteractor>() }

    // ViewModel для плеера
    viewModel { (track: Track) ->
        PlayerViewModel(
            track = track,
            audioPlayerInteractor = get(),
            favoriteTracksInteractor = get(),
            playlistsInteractor = get()
        )
    }
}
