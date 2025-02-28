package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.convertor.PlaylistDbConvertor.toPlaylist
import com.example.playlistmaker.media.data.convertor.PlaylistDbConvertor.toPlaylistEntity
import com.example.playlistmaker.media.data.convertor.TrackInPlaylistDbConvertor.toTrackInPlaylistEntity
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.PlaylistsRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase) : PlaylistsRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist,
        tracksId: List<Int>,
        track: Track
    ) {
        appDatabase.getTrackInPlaylistDao().insertTrackInPlaylist(track.toTrackInPlaylistEntity())
        val updatedTracksId = tracksId+ track.trackId
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = updatedTracksId,
            tracksCount = playlist.tracksCount + 1
        )
        appDatabase.getPlaylistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
    }

    override fun getSavedPlaylists(): Flow<List<Playlist>> {
        return appDatabase.getPlaylistDao().getSavedPlaylists().map {
            it.map { entity -> entity.toPlaylist() }
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        appDatabase.getPlaylistDao().updatePlaylist(playlist.toPlaylistEntity())
    }
}