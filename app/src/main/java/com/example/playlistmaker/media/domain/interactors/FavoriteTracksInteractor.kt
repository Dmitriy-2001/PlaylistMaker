package com.example.playlistmaker.media.domain.interactors

import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractor(private val repository: FavoriteTracksRepository) {

    fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    suspend fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
    }

    suspend fun removeTrackFromFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
    }

    fun getFavoriteTrackIds(): Flow<List<Int>> {
        return repository.getFavoriteTrackIds()
    }
}
