package com.example.playlistmaker.data

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate


const val SHARED_PREFERENCES = "shared_preferences"
const val KEY_FOR_APP_THEME = "key_for_app_theme"

class App: Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)

        switchTheme(sharedPreferences.getBoolean(KEY_FOR_APP_THEME, false))
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

    }
}