package com.example.playlistmaker.settings.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.playlistmaker.App
import com.example.playlistmaker.EMAIL_ADDRESS
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.domain.interfaces.ThemeStateInteractor
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageInteractor

class SettingsViewModel(
    val app: Application,
    private val themeStateInteractor: ThemeStateInteractor,
    private val stringStorageInteractor: StringStorageInteractor
): AndroidViewModel(app) {

    fun getThemeState(): Boolean {
        return themeStateInteractor.getThemeState()
    }

    fun saveAndChangeThemeState(state: Boolean) {
        (app as App).switchTheme(state)
        themeStateInteractor.saveThemeState(state)
    }

    fun getLinkToCourse(): String {
        return stringStorageInteractor.getString(R.string.my_mail)
    }

    fun getEmailMessage(): String {
        return stringStorageInteractor.getString(R.string.thanks_message)
    }

    fun getEmailSubject(): String {
        return stringStorageInteractor.getString(R.string.support_message)
    }

    fun getPracticumOffer(): String {
        return stringStorageInteractor.getString(R.string.user_agreement_url)
    }

    fun getArrayOfEmailAddresses(): Array<String> {
        return arrayOf(EMAIL_ADDRESS)
    }
}