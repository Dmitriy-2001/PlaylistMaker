package com.example.playlistmaker.search.data.repository


import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.domain.interfaces.TracksSearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TracksSearchSearchRepositoryImpl(private val networkClient: NetworkClient):
    TracksSearchRepository {
    override fun searchTracks(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TrackRequest(expression))
        when(response.resultCode) {
            -1 -> {
                emit(Resource.Error(isFailed = false))
            }
            200 -> {
                emit(Resource.Success((response as TrackResponse).tracks.map {
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
                    )}))
            }
            else -> {emit(Resource.Error(isFailed = true))
        }
     }
  }
}