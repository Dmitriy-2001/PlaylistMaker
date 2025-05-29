package com.example.playlistmaker.media.domain.repository

import android.net.Uri

interface LocalStorageRepository {

    fun saveImageToLocalStorage(uri: Uri): Uri
}