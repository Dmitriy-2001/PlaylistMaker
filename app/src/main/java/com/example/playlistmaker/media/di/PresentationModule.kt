package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.presentation.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel { FavoriteTracksViewModel(get()) }
}