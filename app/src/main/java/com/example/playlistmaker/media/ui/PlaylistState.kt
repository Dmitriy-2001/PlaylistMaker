package com.example.playlistmaker.media.ui

import com.example.playlistmaker.media.domain.model.Playlist

sealed interface PlaylistState {
    data class Content(
        val playlists: List<Playlist>
    ) : PlaylistState

    object Empty : PlaylistState
}