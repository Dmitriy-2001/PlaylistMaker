package com.example.playlistmaker.media.domain.interactors

import android.net.Uri

interface LocalStorageInteractor {

    fun saveImageToLocalStorage(uri: Uri): Uri
}