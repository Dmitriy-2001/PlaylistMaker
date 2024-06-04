package com.example.playlistmaker

import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksInteractorImpl
import com.example.playlistmaker.domain.api.TracksRepository

object Creator {

    fun provideTracksInteractor(context: Context): TracksInteractor {
        val networkClient = RetrofitNetworkClient(context)
        val tracksRepository: TracksRepository = TracksRepositoryImpl(networkClient)
        return TracksInteractorImpl(tracksRepository)
    }
}


