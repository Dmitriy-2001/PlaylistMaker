package com.example.playlistmaker.di

import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.data.repository.FavoriteTracksRepositoryImpl
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.media.presentation.MedialibraryFavouritesViewModel
import com.example.playlistmaker.media.presentation.MedialibraryPlaylistsViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val medialibraryModule = module {

    viewModel { MedialibraryFavouritesViewModel(get()) }
    factory { (onTrackClick: (Track) -> Unit) -> TrackAdapter(onTrackClick) }

    viewModel {
        MedialibraryPlaylistsViewModel()
    }
    // Регистрация репозитория избранных треков
    single<FavoriteTracksRepository> {
        FavoriteTracksRepositoryImpl(dao = get())
    }

    // Определение миграции базы данных
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Добавляем новое поле timestamp с типом INTEGER и значением по умолчанию 0
            database.execSQL("ALTER TABLE favorite_tracks ADD COLUMN timestamp INTEGER DEFAULT 0 NOT NULL")
        }
    }

    // Определение базы данных с миграцией
    single {
        Room.databaseBuilder(
            get(),
            AppDatabase::class.java,
            "app_database" // Имя БД
        )
            .addMigrations(MIGRATION_1_2) // Добавляем миграцию
            .build()
    }

    // Определение DAO
    single { get<AppDatabase>().favoriteTracksDao() }
}
