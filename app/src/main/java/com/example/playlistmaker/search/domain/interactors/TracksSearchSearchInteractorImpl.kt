package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import java.util.concurrent.Executors

class TracksSearchSearchInteractorImpl(private val tracksSearchRepository: TracksSearchRepository):
    TracksSearchInteractor {
    private val executor = Executors.newCachedThreadPool()
    override fun searchTracks(expression: String, tracksConsumer: TracksSearchInteractor.TracksConsumer) {
        executor.execute {
            when(val resource = tracksSearchRepository.searchTracks(expression)) {
                is Resource.Success -> { tracksConsumer.consume(foundTracks = resource.data as List<Track>?, isFailed = null) }
                is Resource.Error -> { tracksConsumer.consume(foundTracks = null, isFailed = resource.isFailed) }
            }
        }
    }
}