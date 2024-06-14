package com.example.playlistmaker.settings.domain.interfaces

interface ThemeStateInteractor {
    fun getThemeState(): Boolean

    fun saveThemeState(state: Boolean)
}