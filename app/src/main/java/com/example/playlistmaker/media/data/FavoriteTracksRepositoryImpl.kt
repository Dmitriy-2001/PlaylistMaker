package com.example.playlistmaker.media.data

import com.example.playlistmaker.media.db.FavoriteTracksDao
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import kotlinx.coroutines.flow.Flow

class FavoriteTracksRepositoryImpl(
    private val favoriteTracksDao: FavoriteTracksDao
) : FavoriteTracksRepository {
    override suspend fun addTrack(track: TrackEntity) {
        favoriteTracksDao.addTrackToFavorites(track)
    }
    override suspend fun removeTrack(track: TrackEntity) {
        favoriteTracksDao.removeTrackFromFavorites(track)
    }
    override fun getFavoriteTracks(): Flow<List<TrackEntity>> {
        return favoriteTracksDao.getAllFavoriteTracks()
    }
    override suspend fun getAllFavoriteTrackIds(): List<String> {
        return favoriteTracksDao.getAllFavoriteTrackIds()
    }
}