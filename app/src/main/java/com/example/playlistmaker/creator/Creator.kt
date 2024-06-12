package com.example.playlistmaker.creator

import android.content.Context
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.impl.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.TracksInteractor
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.search.data.TracksRepository

object Creator {

    fun provideTracksInteractor(context: Context): TracksInteractor {
        val networkClient = RetrofitNetworkClient(context)
        val tracksRepository: TracksRepository = TracksRepositoryImpl(networkClient)
        return TracksInteractorImpl(tracksRepository)
    }
}


