package com.example.playlistmaker.domain.api

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, tracksConsumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>, isFailed: Boolean)
    }
}