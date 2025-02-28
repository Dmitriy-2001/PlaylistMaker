package com.example.playlistmaker.media.data.convertor

import com.example.playlistmaker.media.data.db.TrackInPlaylistEntity
import com.example.playlistmaker.search.domain.models.Track

object TrackInPlaylistDbConvertor {
    fun Track.toTrackInPlaylistEntity(): TrackInPlaylistEntity {
        return TrackInPlaylistEntity(
            trackId,
            trackName,
            artistName,
            trackTime,
            artworkUrl100,
            collectionName,
            releaseDate,
            primaryGenreName,
            country,
            previewUrl
        )
    }
}