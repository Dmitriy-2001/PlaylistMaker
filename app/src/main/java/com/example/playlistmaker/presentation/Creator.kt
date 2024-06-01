package com.example.playlistmaker.presentation

import android.content.Context
import com.example.playlistmaker.data.TracksRepositoryImpl
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.TracksInteractorImpl

object Creator {
    fun provideTracksInteractor(context: Context): TracksInteractor {
        val networkClient = RetrofitNetworkClient(context)
        val repository = TracksRepositoryImpl(networkClient)
        return TracksInteractorImpl(repository)
    }
}
