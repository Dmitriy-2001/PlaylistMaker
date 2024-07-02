package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.models.Track

data class TracksState(
    val tracks: List<Track>,
    val isLoading: Boolean,
    val isFailed: Boolean?
)