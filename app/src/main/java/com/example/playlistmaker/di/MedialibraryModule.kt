package com.example.playlistmaker.di

import com.example.playlistmaker.media.presentation.MedialibraryFavouritesViewModel
import com.example.playlistmaker.media.presentation.MedialibraryPlaylistsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val medialibraryModule = module {

    viewModel {
        MedialibraryFavouritesViewModel()
    }

    viewModel {
        MedialibraryPlaylistsViewModel()
    }
}