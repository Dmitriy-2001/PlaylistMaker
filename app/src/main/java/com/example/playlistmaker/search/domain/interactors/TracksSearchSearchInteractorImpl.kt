package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


class TracksSearchSearchInteractorImpl(private val tracksSearchRepository: TracksSearchRepository) :
    TracksSearchInteractor {
    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, Boolean?>> {
        return tracksSearchRepository.searchTracks(expression).map { result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.isFailed)
                }
            }
        }
    }
}