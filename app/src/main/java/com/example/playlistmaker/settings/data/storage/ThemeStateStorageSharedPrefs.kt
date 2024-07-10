package com.example.playlistmaker.settings.data.storage

import android.content.SharedPreferences
import com.example.playlistmaker.KEY_FOR_APP_THEME

class ThemeStateStorageSharedPrefs(private val sharedPreferences: SharedPreferences): ThemeStateStorage {
    override fun getThemeStateStorage(): Boolean {
        return sharedPreferences.getBoolean(KEY_FOR_APP_THEME, false)
    }

    override fun saveThemeStateStorage(state: Boolean) {
        sharedPreferences.edit()
            .putBoolean(KEY_FOR_APP_THEME, state)
            .apply()
    }
}