package com.example.playlistmaker.media.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrackToFavorites(track: TrackEntity)
    @Delete
    suspend fun removeTrackFromFavorites(track: TrackEntity)
    @Query("SELECT * FROM favorite_tracks ORDER BY trackId DESC")
    fun getAllFavoriteTracks(): Flow<List<TrackEntity>>
    @Query("SELECT trackId FROM favorite_tracks")
    suspend fun getAllFavoriteTrackIds(): List<String>
}