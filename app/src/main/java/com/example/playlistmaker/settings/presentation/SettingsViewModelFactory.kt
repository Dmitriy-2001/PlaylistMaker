package com.example.playlistmaker.settings.presentation

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.utils.Creator

class SettingsViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val themeStateInteractor = Creator.provideThemeStateInteractor(context)
    private val stringStorageInteractor = Creator.provideStringStorageInteractor(context)
    private val app = context.applicationContext as Application

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingsViewModel(app, themeStateInteractor, stringStorageInteractor) as T
    }

}