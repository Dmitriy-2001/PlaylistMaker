package com.example.playlistmaker.search.domain.interfaces

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface HistoryTrackRepositorySH {
    fun getTrackListFromSH(): Flow<Array<Track>>
    fun saveTrackListToSH(historyList: ArrayList<Track>)
}