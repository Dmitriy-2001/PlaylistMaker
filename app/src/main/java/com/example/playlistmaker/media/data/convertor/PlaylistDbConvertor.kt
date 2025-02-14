package com.example.playlistmaker.media.data.convertor

import androidx.room.TypeConverter
import com.example.playlistmaker.media.data.db.PlaylistEntity
import com.example.playlistmaker.media.domain.model.Playlist
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PlaylistDbConvertor {
    fun Playlist.toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistId,
            playlistName,
            playlistDescription,
            uri,
            fromListIntToString(tracksIdInPlaylist),
            tracksCount
        )
    }

    fun PlaylistEntity.toPlaylist(): Playlist {
        return Playlist(
            playlistId,
            playlistName,
            playlistDescription,
            uri,
            fromStringToListInt(tracksIdInPlaylist),
            tracksCount
        )
    }

    @TypeConverter
    private fun fromListIntToString(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    private fun fromStringToListInt(value: String): List<Int> {
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }
}