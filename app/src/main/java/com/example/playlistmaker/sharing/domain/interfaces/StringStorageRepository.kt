package com.example.playlistmaker.sharing.domain.interfaces

interface StringStorageRepository {
    fun getStringById(id: Int): String
}