package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.storage.TrackSearchHistoryStorage
import com.example.playlistmaker.search.domain.interfaces.HistoryTrackRepositorySH
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HistoryTrackRepositorySHImpl (private val trackSearchHistoryStorage: TrackSearchHistoryStorage):
    HistoryTrackRepositorySH {
    override fun getTrackListFromSH(): Flow<Array<Track>> = flow {
        val tracksDto = trackSearchHistoryStorage.getTracksFromStorage()
        val tracks: Array<Track> =  tracksDto.map {
            Track(
                trackId = it.trackId,
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

        }.toTypedArray()

        emit(tracks)
    }

    override fun saveTrackListToSH(historyList: ArrayList<Track>) {
        val tracksDto = historyList.map {
            TrackDto(
                trackId = it.trackId,
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

        trackSearchHistoryStorage.saveTracksToStorage(ArrayList(tracksDto))
    }

}