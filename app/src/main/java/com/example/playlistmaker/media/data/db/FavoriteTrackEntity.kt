package com.example.playlistmaker.media.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

@Entity(tableName = "favorite_tracks")
data class FavoriteTrackEntity(
    @PrimaryKey val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: Long,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    val timestamp: Long,
    val createdAt: Long? = Instant.now().toEpochMilli(),
)