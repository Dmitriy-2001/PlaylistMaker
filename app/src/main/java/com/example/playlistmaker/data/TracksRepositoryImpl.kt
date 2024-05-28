package com.example.playlistmaker.data

import com.example.playlistmaker.domain.api.TracksRepository
import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse

class TracksRepositoryImpl(private val networkClient: RetrofitNetworkClient) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        return try {
            val trackRequest = TrackRequest(expression)
            val response = networkClient.doRequest(trackRequest)
            if (response.resultCode == 200) {
                val trackResponse = response as TrackResponse
                Resource.Success(trackResponse.tracks)
            } else {
                Resource.Error(true)
            }
        } catch (e: Exception) {
            Resource.Error(true)
        }
    }
}

