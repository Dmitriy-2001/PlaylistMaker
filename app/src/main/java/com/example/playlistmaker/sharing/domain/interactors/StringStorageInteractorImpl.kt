package com.example.playlistmaker.sharing.domain.interactors

import com.example.playlistmaker.sharing.domain.interfaces.StringStorageInteractor
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageRepository

class StringStorageInteractorImpl(val stringStorageRepository: StringStorageRepository):
    StringStorageInteractor {
    override fun getString(id: Int): String {
        return stringStorageRepository.getStringById(id)
    }
}