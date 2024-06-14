package com.example.playlistmaker.settings.data.repository

import com.example.playlistmaker.settings.data.storage.ThemeStateStorage
import com.example.playlistmaker.settings.domain.interfaces.ThemeStateRepository

class ThemeStateRepositoryImpl (val themeStateStorage: ThemeStateStorage): ThemeStateRepository {
    override fun getThemeStateData(): Boolean {
        return themeStateStorage.getThemeStateStorage()
    }

    override fun saveThemeStateData(state: Boolean) {
        themeStateStorage.saveThemeStateStorage(state)
    }
}