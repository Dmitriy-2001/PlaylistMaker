package com.example.playlistmaker.di

import com.example.playlistmaker.sharing.data.repository.StringStorageRepositoryImpl
import com.example.playlistmaker.sharing.data.storage.StringStorage
import com.example.playlistmaker.sharing.data.storage.StringStorageFromSystemResources
import com.example.playlistmaker.sharing.domain.interactors.StringStorageInteractorImpl
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageInteractor
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val sharingModule = module {
    single<StringStorage> {
        StringStorageFromSystemResources(context = androidContext())
    }

    single<StringStorageRepository> {
        StringStorageRepositoryImpl(stringStorage = get())
    }

    factory<StringStorageInteractor> {
        StringStorageInteractorImpl(stringStorageRepository = get())
    }
}