package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    suspend fun createPlaylist(playlist: Playlist)
    suspend fun addTrackIdInPlaylist(playlist: Playlist, tracksId: List<Int>, track: Track)
    fun getSavedPlaylists(): Flow<List<Playlist>>
    suspend fun updatePlaylist(playlist: Playlist)
    fun getSavedPlaylist(id: Int): Flow<Playlist>
    fun getPlaylistFromJson(json: String): Playlist?
    fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>>
    suspend fun deletePlaylist(playlist: Playlist)
    suspend fun removeTrack(playlist: Playlist, trackId: Int)
    suspend fun getPlaylistById(playlistId: Int): Playlist?
}