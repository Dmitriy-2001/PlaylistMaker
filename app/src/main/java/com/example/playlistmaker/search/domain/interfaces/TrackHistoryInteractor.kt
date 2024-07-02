package com.example.playlistmaker.search.domain.interfaces

import com.example.playlistmaker.search.domain.models.Track

interface TrackHistoryInteractor {
    fun getHistoryList(): ArrayList<Track>
    fun saveHistoryList()
    fun addTrackToHistoryList(track: Track)
    fun clearHistoryList()
    fun transferToTop(track: Track): Int

}