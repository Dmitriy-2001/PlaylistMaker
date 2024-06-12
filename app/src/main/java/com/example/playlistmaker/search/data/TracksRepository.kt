package com.example.playlistmaker.search.data

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    suspend fun searchTracks(expression: String): Resource<List<Track>>
}
