package com.example.playlistmaker.data.mapper

import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.domain.models.Track

fun TrackDto.toDomainModel(): Track {
    return Track(
        trackId = this.trackId,
        trackName = this.trackName,
        artistName = this.artistName,
        trackTime = this.trackTime,
        artworkUrl = this.artworkUrl,
        collectionName = this.collectionName,
        releaseDate = this.releaseDate,
        primaryGenreName = this.primaryGenreName,
        country = this.country,
        previewUrl = this.previewUrl
    )
}

fun TrackResponse.toDomainModelList(): List<Track> {
    return this.tracks.map { it.toDomainModel() }
}
