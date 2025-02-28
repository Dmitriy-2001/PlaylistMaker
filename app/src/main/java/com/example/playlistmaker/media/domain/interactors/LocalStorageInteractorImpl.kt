package com.example.playlistmaker.media.domain.interactors

import android.net.Uri
import com.example.playlistmaker.media.domain.repository.LocalStorageRepository

class LocalStorageInteractorImpl(private val localStorageRepository: LocalStorageRepository) :
    LocalStorageInteractor {

    override fun saveImageToLocalStorage(uri: Uri): Uri {
        return localStorageRepository.saveImageToLocalStorage(uri)
    }
}