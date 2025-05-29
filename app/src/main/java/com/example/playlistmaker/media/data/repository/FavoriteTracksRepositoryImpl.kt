package com.example.playlistmaker.media.data.repository

import com.example.playlistmaker.media.data.db.FavoriteTrackEntity
import com.example.playlistmaker.media.data.db.FavoriteTracksDao
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoriteTracksRepositoryImpl(private val dao: FavoriteTracksDao) : FavoriteTracksRepository {
    override fun getFavoriteTracks(): Flow<List<Track>> {
        return dao.getAllFavorites().map { entities ->
            entities.map {
                Track(
                    trackId = it.trackId, // Int
                    trackName = it.trackName,
                    artistName = it.artistName,
                    trackTime = it.trackTime,
                    artworkUrl100 = it.artworkUrl100,
                    collectionName = it.collectionName,
                    releaseDate = it.releaseDate,
                    primaryGenreName = it.primaryGenreName,
                    country = it.country,
                    previewUrl = it.previewUrl
                )
            }
        }
    }

    override suspend fun addTrackToFavorites(track: Track) {
        dao.insertTrack(
            FavoriteTrackEntity(
                trackId = track.trackId, // Int
                trackName = track.trackName ?: "",
                artistName = track.artistName ?: "",
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override suspend fun removeTrackFromFavorites(track: Track) {
        dao.deleteTrack(
            FavoriteTrackEntity(
                trackId = track.trackId, // Int
                trackName = track.trackName ?: "",
                artistName = track.artistName ?: "",
                trackTime = track.trackTime,
                artworkUrl100 = track.artworkUrl100,
                collectionName = track.collectionName,
                releaseDate = track.releaseDate,
                primaryGenreName = track.primaryGenreName,
                country = track.country,
                previewUrl = track.previewUrl,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    override fun getFavoriteTrackIds(): Flow<List<Int>> {
        return dao.getFavoriteTrackIds() // Int
    }
}
