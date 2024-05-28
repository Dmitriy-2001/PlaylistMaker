package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.models.Resource

interface TracksRepository {
    fun searchTracks(expression: String): Resource<List<Track>>
}