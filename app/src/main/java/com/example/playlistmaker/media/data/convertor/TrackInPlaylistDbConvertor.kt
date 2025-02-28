package com.example.playlistmaker.media.data.convertor

import com.example.playlistmaker.media.data.db.TrackInPlaylistEntity
import com.example.playlistmaker.media.domain.model.Track


object TrackInPlaylistDbConvertor {
    fun Track.toTrackInPlaylistEntity(): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId,
            trackName ?: "",
            artistName ?: "",
            trackTime,
            artworkUrl100 ?: "",
            collectionName ?: "",
            releaseDate ?: "",
            primaryGenreName ?: "",
            country ?: "",
            previewUrl
        )
    }

    fun TrackInPlaylistEntity.toTrack(): Track {
        return Track(
            trackId,
            trackName,
            artistName,
            trackTime,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl,
        )
    }
}