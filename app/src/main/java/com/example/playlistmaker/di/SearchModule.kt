package com.example.playlistmaker.di

import android.content.Context
import com.example.playlistmaker.BASE_URL
import com.example.playlistmaker.SHARED_PREFERENCES
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.HistoryTrackRepositorySHImpl
import com.example.playlistmaker.search.data.repository.TracksSearchSearchRepositoryImpl
import com.example.playlistmaker.search.data.storage.TrackSearchHistoryStorage
import com.example.playlistmaker.search.data.storage.TrackSearchHistoryStorageSharedPrefs
import com.example.playlistmaker.search.domain.interactors.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactors.TracksSearchSearchInteractorImpl
import com.example.playlistmaker.search.domain.interfaces.HistoryTrackRepositorySH
import com.example.playlistmaker.search.domain.interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchRepository
import com.example.playlistmaker.search.presentation.SearchingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val searchModule = module {
    single {
        androidContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    single<TrackSearchHistoryStorage> {
        TrackSearchHistoryStorageSharedPrefs(sharedPreferences = get())
    }

    single<HistoryTrackRepositorySH> {
        HistoryTrackRepositorySHImpl(trackSearchHistoryStorage = get())
    }

    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single<NetworkClient> {
        RetrofitNetworkClient(context = androidContext(), trackService = get())
    }

    single<TracksSearchRepository> {
        TracksSearchSearchRepositoryImpl(networkClient = get())
    }

    factory<TrackHistoryInteractor> {
        TrackHistoryInteractorImpl(historyTrackRepositorySH = get())
    }

    factory<TracksSearchInteractor> {
        TracksSearchSearchInteractorImpl(tracksSearchRepository = get())
    }

    viewModel {
        SearchingViewModel(tracksSearchInteractor = get(), trackHistoryInteractor = get())
    }

}