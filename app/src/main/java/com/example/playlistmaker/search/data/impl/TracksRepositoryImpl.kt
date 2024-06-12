package com.example.playlistmaker.search.data.impl

import com.example.playlistmaker.data.mapper.toDomainModelList
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.TracksRepository
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource

class TracksRepositoryImpl(private val networkClient: NetworkClient) : TracksRepository {

    override suspend fun searchTracks(expression: String): Resource<List<Track>> {
        val trackRequest = TrackRequest(expression)
        val response = networkClient.doRequest(trackRequest)

        return if (response.resultCode != 200) {
            Resource.Error("Error code: ${response.resultCode}")
        } else {
            val trackResponse = response as TrackResponse
            Resource.Success(trackResponse.toDomainModelList())
        }
    }
}




