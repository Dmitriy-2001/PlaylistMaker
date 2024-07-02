package com.example.playlistmaker.sharing.data.repository

import com.example.playlistmaker.sharing.data.storage.StringStorage
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageRepository

class StringStorageRepositoryImpl(val stringStorage: StringStorage): StringStorageRepository {
    override fun getStringById(id: Int): String {
        return stringStorage.getStringFromStorage(id)
    }
}