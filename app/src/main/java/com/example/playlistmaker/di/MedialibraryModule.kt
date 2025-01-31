package com.example.playlistmaker.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.media.presentation.MedialibraryFavouritesViewModel
import com.example.playlistmaker.media.presentation.MedialibraryPlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val medialibraryModule = module {

    // DAO
    single { get<AppDatabase>().favoriteTracksDao() }

    // Репозиторий избранных треков
    single<FavoriteTracksRepository> { FavoriteTracksRepositoryImpl(dao = get()) }

    // Интерактор избранных треков
    single { FavoriteTracksInteractor(repository = get()) }

    // ViewModel для избранного
    viewModel { MedialibraryFavouritesViewModel(favoriteTracksInteractor = get()) }

    // ViewModel для плейлистов
    viewModel { MedialibraryPlaylistsViewModel() }

    // Адаптер для треков
    factory { (onTrackClick: (Track) -> Unit) -> TrackAdapter(onTrackClick) }

    // Миграция базы данных
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("ALTER TABLE favorite_tracks ADD COLUMN timestamp INTEGER DEFAULT 0 NOT NULL")
        }
    }

    // База данных
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "app_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
}
