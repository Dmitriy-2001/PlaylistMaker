package com.example.playlistmaker.search.data.storage

import com.example.playlistmaker.search.data.dto.TrackDto

interface TrackSearchHistoryStorage {
    fun getTracksFromStorage(): Array<TrackDto>

    fun saveTracksToStorage(tracks: ArrayList<TrackDto>)
}