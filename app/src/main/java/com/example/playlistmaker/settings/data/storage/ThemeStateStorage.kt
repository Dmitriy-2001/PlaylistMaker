package com.example.playlistmaker.settings.data.storage

interface ThemeStateStorage {

    fun getThemeStateStorage(): Boolean

    fun saveThemeStateStorage(state: Boolean)

}