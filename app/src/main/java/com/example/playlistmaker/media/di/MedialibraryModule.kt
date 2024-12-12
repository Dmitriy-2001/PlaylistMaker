package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.FavoriteTracksViewModel
import com.example.playlistmaker.media.presentation.MedialibraryPlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val medialibraryModule = module {

    viewModel {
        FavoriteTracksViewModel(get())
    }

    viewModel {
        MedialibraryPlaylistsViewModel()
    }
}