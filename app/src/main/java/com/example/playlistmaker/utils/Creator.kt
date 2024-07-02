package com.example.playlistmaker.utils

import android.content.Context
import com.example.playlistmaker.player.data.repository.AudioPlayerRepositoryImpl
import com.example.playlistmaker.player.domain.interactors.AudioPlayerInteractorImpl
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerRepository
import com.example.playlistmaker.player.domain.models.PlayerTrack
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.data.repository.HistoryTrackRepositorySHImpl
import com.example.playlistmaker.search.data.repository.TracksSearchSearchRepositoryImpl
import com.example.playlistmaker.search.data.storage.TrackSearchHistoryStorageSharedPrefs
import com.example.playlistmaker.search.domain.interactors.TrackHistoryInteractorImpl
import com.example.playlistmaker.search.domain.interactors.TracksSearchSearchInteractorImpl
import com.example.playlistmaker.search.domain.interfaces.HistoryTrackRepositorySH
import com.example.playlistmaker.search.domain.interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchRepository
import com.example.playlistmaker.settings.data.repository.ThemeStateRepositoryImpl
import com.example.playlistmaker.settings.data.storage.ThemeStateStorageSharedPrefs
import com.example.playlistmaker.settings.domain.interactors.ThemeStateInteractorImpl
import com.example.playlistmaker.settings.domain.interfaces.ThemeStateInteractor
import com.example.playlistmaker.settings.domain.interfaces.ThemeStateRepository
import com.example.playlistmaker.sharing.data.repository.StringStorageRepositoryImpl
import com.example.playlistmaker.sharing.data.storage.StringStorageFromSystemResources
import com.example.playlistmaker.sharing.domain.interactors.StringStorageInteractorImpl
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageInteractor
import com.example.playlistmaker.sharing.domain.interfaces.StringStorageRepository

object Creator {
    private fun getThemeStateRepository(context: Context): ThemeStateRepository {
        return ThemeStateRepositoryImpl(ThemeStateStorageSharedPrefs(context))
    }

    fun provideThemeStateInteractor(context: Context): ThemeStateInteractor {
        return ThemeStateInteractorImpl(getThemeStateRepository(context))
    }

    private fun getStringStorageRepository(context: Context): StringStorageRepository {
        return StringStorageRepositoryImpl(StringStorageFromSystemResources(context))
    }

    fun provideStringStorageInteractor(context: Context): StringStorageInteractor {
        return StringStorageInteractorImpl(getStringStorageRepository(context))
    }

    private fun getTracksSearchRepository(context: Context): TracksSearchRepository {
        return TracksSearchSearchRepositoryImpl(RetrofitNetworkClient(context))
    }

    fun provideTracksSearchInteractor(context: Context): TracksSearchInteractor {
        return TracksSearchSearchInteractorImpl(getTracksSearchRepository(context))
    }

    private fun getHistoryTrackRepositorySH(context: Context): HistoryTrackRepositorySH {
        return HistoryTrackRepositorySHImpl(TrackSearchHistoryStorageSharedPrefs(context))
    }

    fun provideTrackHistoryInteractor(context: Context): TrackHistoryInteractor {
        return TrackHistoryInteractorImpl(getHistoryTrackRepositorySH(context))
    }



    private fun getAudioPlayerRepository(): AudioPlayerRepository {
        return AudioPlayerRepositoryImpl()
    }

    fun provideAudioPlayerInteractor(playerTrack: PlayerTrack): AudioPlayerInteractor {
        return AudioPlayerInteractorImpl(playerTrack, getAudioPlayerRepository())
    }


}


