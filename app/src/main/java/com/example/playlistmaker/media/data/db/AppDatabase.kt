package com.example.playlistmaker.media.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [FavoriteTrackEntity::class, PlaylistEntity::class, TrackInPlaylistEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favoriteTracksDao(): FavoriteTracksDao
    abstract fun getPlaylistDao(): PlaylistDao
    abstract fun getTrackInPlaylistDao(): TrackInPlaylistDao
}
