package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist

class GetPlaylistUseCase(private val repository: PlaylistsRepository) {
    fun execute(json: String): Playlist? {
        return repository.getPlaylistFromJson(json)
    }
}