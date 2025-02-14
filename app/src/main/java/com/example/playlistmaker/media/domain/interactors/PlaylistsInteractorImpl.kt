package com.example.playlistmaker.media.domain.interactors

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistsRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(private val playlistsRepository: PlaylistsRepository) :
    PlaylistsInteractor {
    override suspend fun createPlaylist(playlist: Playlist) {
        playlistsRepository.createPlaylist(playlist)
    }

    override suspend fun addTracksIdInPlaylist(
        playlist: Playlist,
        tracksId: List<Int>,
        track: Track
    ) {
        playlistsRepository.addTrackIdInPlaylist(playlist, tracksId as ArrayList<Int>, track)
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getSavedPlaylists()
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistsRepository.updatePlaylist(playlist)
    }
}