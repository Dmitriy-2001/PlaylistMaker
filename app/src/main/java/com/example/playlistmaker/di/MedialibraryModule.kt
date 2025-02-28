package com.example.playlistmaker.di

import androidx.room.Room
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.data.repository.LocalStorageRepositoryImpl
import com.example.playlistmaker.media.data.repository.PlaylistsRepositoryImpl
import com.example.playlistmaker.media.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.interactors.LocalStorageInteractor
import com.example.playlistmaker.media.domain.interactors.LocalStorageInteractorImpl
import com.example.playlistmaker.media.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.media.domain.interactors.PlaylistsInteractorImpl
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.media.domain.repository.GetPlaylistUseCase
import com.example.playlistmaker.media.domain.repository.LocalStorageRepository
import com.example.playlistmaker.media.domain.repository.PlaylistsRepository
import com.example.playlistmaker.media.presentation.EditPlaylistViewModel
import com.example.playlistmaker.media.presentation.MedialibraryFavouritesViewModel
import com.example.playlistmaker.media.presentation.MedialibraryPlaylistsViewModel
import com.example.playlistmaker.media.presentation.NewPlaylistViewModel
import com.example.playlistmaker.media.presentation.ViewPlaylistViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.google.gson.Gson
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val medialibraryModule = module {
    // DAO
    single { get<AppDatabase>().favoriteTracksDao() }
    single { get<AppDatabase>().getPlaylistDao() }
    single { get<AppDatabase>().getTrackInPlaylistDao() }

    // Репозитории
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(dao = get()) }
    single<LocalStorageRepository> { LocalStorageRepositoryImpl(get()) }
    single<PlaylistsRepository> { PlaylistsRepositoryImpl(get(), get()) }

    // Интеракторы
    single { FavoriteTracksInteractor(repository = get()) }
    single<LocalStorageInteractor> { LocalStorageInteractorImpl(get()) }
    single<PlaylistsInteractor> { PlaylistsInteractorImpl(get()) }

    // UseCase
    factory { GetPlaylistUseCase(get()) }

    // ViewModel
    viewModel { MedialibraryFavouritesViewModel(favoriteTracksInteractor = get()) }
    viewModel { MedialibraryPlaylistsViewModel(get()) }
    viewModel { NewPlaylistViewModel(get(), get()) }
    viewModel { EditPlaylistViewModel(get(), get(), get()) }
    viewModel { ViewPlaylistViewModel(androidApplication(), get(), get()) }

    // Адаптер для треков
    factory { (onTrackClick: (Track) -> Unit) -> TrackAdapter(onTrackClick) }

    // База данных
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    // Gson
    single { Gson() }
}
