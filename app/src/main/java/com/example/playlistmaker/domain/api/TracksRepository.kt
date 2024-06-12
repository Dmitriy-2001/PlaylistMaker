package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.utils.Resource

interface TracksRepository {
    suspend fun searchTracks(expression: String): Resource<List<Track>>
}
