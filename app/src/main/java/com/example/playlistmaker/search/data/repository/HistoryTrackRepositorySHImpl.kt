package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.storage.TrackSearchHistoryStorage
import com.example.playlistmaker.search.domain.interfaces.HistoryTrackRepositorySH
import com.example.playlistmaker.search.domain.models.Track

class HistoryTrackRepositorySHImpl (private val trackSearchHistoryStorage: TrackSearchHistoryStorage):
    HistoryTrackRepositorySH {
    override fun getTrackListFromSH(): Array<Track> {
        val tracksDto = trackSearchHistoryStorage.getTracksFromStorage()
        val tracks: Array<Track> =  tracksDto.map {
            Track(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTime = it.trackTime,
                artworkUrl = it.artworkUrl,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl
            )
        }.toTypedArray()

        return tracks
    }

    override fun saveTrackListToSH(historyList: ArrayList<Track>) {
        val tracksDto = historyList.map {
            TrackDto(
                trackId = it.trackId,
                trackName = it.trackName,
                artistName = it.artistName,
                trackTime = it.trackTime,
                artworkUrl = it.artworkUrl,
                collectionName = it.collectionName,
                releaseDate = it.releaseDate,
                primaryGenreName = it.primaryGenreName,
                country = it.country,
                previewUrl = it.previewUrl
            )
        }

        trackSearchHistoryStorage.saveTracksToStorage(ArrayList(tracksDto))
    }

}