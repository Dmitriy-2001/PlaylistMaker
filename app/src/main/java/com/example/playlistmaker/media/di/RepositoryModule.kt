package com.example.playlistmaker.media.di

import com.example.playlistmaker.media.data.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(get()) }
}