package com.example.playlistmaker.media.data.repository

import android.util.Log
import com.example.playlistmaker.media.data.convertor.PlaylistDbConvertor.toPlaylist
import com.example.playlistmaker.media.data.convertor.PlaylistDbConvertor.toPlaylistEntity
import com.example.playlistmaker.media.data.convertor.TrackInPlaylistDbConvertor.toTrack
import com.example.playlistmaker.media.data.convertor.TrackInPlaylistDbConvertor.toTrackInPlaylistEntity
import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.model.Track
import com.example.playlistmaker.media.domain.repository.PlaylistsRepository
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PlaylistsRepositoryImpl(private val appDatabase: AppDatabase, private val gson: Gson) :
    PlaylistsRepository {
    override suspend fun createPlaylist(playlist: Playlist) {
        Log.d("PlaylistsRepositoryImpl", playlist.toString())
        appDatabase.getPlaylistDao().insertPlaylist(playlist.toPlaylistEntity())
    }

    override suspend fun addTrackIdInPlaylist(
        playlist: Playlist,
        tracksId: List<Int>,
        track: Track,
    ) {
        appDatabase.getTrackInPlaylistDao().insertTrackInPlaylist(track.toTrackInPlaylistEntity())
        val updatedTracksId = tracksId + track.trackId
        val updatedPlaylist = playlist.copy(
            tracksIdInPlaylist = updatedTracksId,
            tracksCount = updatedTracksId.size
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
    override fun getSavedPlaylist(id: Int): Flow<Playlist> {
        return appDatabase.getPlaylistDao().getSavedPlaylist(id).map { it.toPlaylist() }
    }

    override fun getPlaylistFromJson(json: String): Playlist? {
        return gson.fromJson(json, Playlist::class.java)
    }

    override fun getTracksInPlaylist(trackIds: List<Int>): Flow<List<Track>> {
        return appDatabase.getTrackInPlaylistDao().getTracksInPlaylist(trackIds)
            .map { tracks -> tracks.map { it.toTrack() } }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val tracks = playlist.tracksIdInPlaylist
        appDatabase.getPlaylistDao().removePlaylist(playlist.playlistId)
        val plalists = appDatabase.getPlaylistDao().getSavedPlaylists().first()
        for (trackId: Int in tracks) {
            var isExist = false
            for (playlistOther in plalists) {
                if (playlistOther.tracksIdInPlaylist.contains(trackId.toString())) isExist = true
            }
            if (!isExist) appDatabase.getTrackInPlaylistDao().removeTrack(trackId)
        }
    }

    override suspend fun removeTrack(playlist: Playlist, trackId: Int) {
        coroutineScope {
            async {
                val playlistDb = appDatabase.getPlaylistDao().getSavedPlaylist(playlist.playlistId)
                    .map { it.toPlaylist() }.first()
                val updatedPlaylist = playlist.copy(
                    tracksIdInPlaylist = playlistDb.tracksIdInPlaylist.filterNot { it == trackId },
                    tracksCount = playlistDb.tracksIdInPlaylist.filterNot { it == trackId }.size
                )
                appDatabase.getPlaylistDao().updatePlaylist(updatedPlaylist.toPlaylistEntity())
            }.await()
            async {
                var isExist = false
                val playlists = appDatabase.getPlaylistDao().getSavedPlaylists().first()
                for (playlistOther in playlists) {
                    if (playlistOther.tracksIdInPlaylist.contains(trackId.toString())) isExist =
                        true
                }
                if (!isExist) appDatabase.getTrackInPlaylistDao().removeTrack(trackId)
            }.await()
        }
    }
    private companion object {
        const val TAG = "PlaylistsRepositoryImpl"
    }
}