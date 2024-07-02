package com.example.playlistmaker.settings.domain.interfaces

interface ThemeStateRepository {
    fun getThemeStateData(): Boolean

    fun saveThemeStateData(state: Boolean)

}