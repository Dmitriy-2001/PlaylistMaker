package com.example.playlistmaker.search.domain.interfaces

import com.example.playlistmaker.search.domain.models.Track

interface HistoryTrackRepositorySH {
    fun getTrackListFromSH(): Array<Track>
    fun saveTrackListToSH(historyList: ArrayList<Track>)
}