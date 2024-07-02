package com.example.playlistmaker.di

import android.app.Application
import com.example.playlistmaker.settings.data.repository.ThemeStateRepositoryImpl
import com.example.playlistmaker.settings.data.storage.ThemeStateStorage
import com.example.playlistmaker.settings.data.storage.ThemeStateStorageSharedPrefs
import com.example.playlistmaker.settings.domain.interactors.ThemeStateInteractorImpl
import com.example.playlistmaker.settings.domain.interfaces.ThemeStateInteractor
import com.example.playlistmaker.settings.domain.interfaces.ThemeStateRepository
import com.example.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val settingsModule = module {
    single<ThemeStateStorage> {
        ThemeStateStorageSharedPrefs(sharedPreferences = get())
    }

    single<ThemeStateRepository> {
        ThemeStateRepositoryImpl(themeStateStorage = get())
    }

    factory<ThemeStateInteractor> {
        ThemeStateInteractorImpl(themeStateRepository = get())
    }

    single {
        androidContext().applicationContext as Application
    }

    viewModel {
        SettingsViewModel(app = get(), themeStateInteractor = get(), stringStorageInteractor = get())
    }

}