package com.example.playlistmaker.search.domain.interfaces

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksSearchInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, Boolean?>>
}