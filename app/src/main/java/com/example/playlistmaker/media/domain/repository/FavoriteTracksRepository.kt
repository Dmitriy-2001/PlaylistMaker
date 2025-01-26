package com.example.playlistmaker.media.domain.repository

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoriteTracksRepository {
    fun getFavoriteTracks(): Flow<List<Track>>
    suspend fun addTrackToFavorites(track: Track)
    suspend fun removeTrackFromFavorites(track: Track)
    fun getFavoriteTrackIds(): Flow<List<Int>>
}
