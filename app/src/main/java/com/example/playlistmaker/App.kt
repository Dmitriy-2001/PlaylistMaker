package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.di.playerModule
import com.example.playlistmaker.di.searchModule
import com.example.playlistmaker.di.settingsModule
import com.example.playlistmaker.di.sharingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin


const val SHARED_PREFERENCES = "shared_preferences"
const val KEY_FOR_APP_THEME = "key_for_app_theme"
const val BASE_URL = "https://itunes.apple.com"
const val KEY_FOR_HISTORY_LIST = "KEY_FOR_HISTORY_LIST"
const val KEY_FOR_PLAYER = "KEY_FOR_PLAYER"
const val EMAIL_ADDRESS = "richard.orlov13@gmail.com"
class App: Application() {

   private var darkTheme = false
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(playerModule, searchModule, settingsModule, sharingModule)
        }
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