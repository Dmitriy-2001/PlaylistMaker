package com.example.playlistmaker.data.repository

import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.network.NetworkClient
import com.example.playlistmaker.data.mapper.toDomainModelList
import com.example.playlistmaker.utils.Resource
import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Track

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




