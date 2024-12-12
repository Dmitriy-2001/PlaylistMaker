package com.example.playlistmaker.media.domain

import com.example.playlistmaker.media.db.TrackEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    suspend fun addTrack(track: TrackEntity)
    suspend fun removeTrack(track: TrackEntity)
    fun getFavoriteTracks(): Flow<List<TrackEntity>>
    suspend fun getAllFavoriteTrackIds(): List<String>
}