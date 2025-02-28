package com.example.playlistmaker.media.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteTracksDao {

    @Query("SELECT * FROM favorite_tracks ORDER BY createdAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: FavoriteTrackEntity)

    @Delete
    suspend fun deleteTrack(track: FavoriteTrackEntity)

    @Query("SELECT trackId FROM favorite_tracks")
    fun getFavoriteTrackIds(): Flow<List<Int>>
}

