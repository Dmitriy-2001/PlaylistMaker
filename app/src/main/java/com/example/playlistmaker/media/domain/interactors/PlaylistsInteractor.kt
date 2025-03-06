package com.example.playlistmaker.media.domain.interactors

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTracksIdInPlaylist(playlist: Playlist, tracksId: List<Int>, track: Track)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    fun getSavedPlaylist(playlistId: Int): Flow<Playlist>
    fun getTracksInPlaylist(tracksId: List<Int>): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun removeTrack(playlist: Playlist, trackId: Int)
    suspend fun getPlaylistById(playlistId: Int): Playlist?
}
