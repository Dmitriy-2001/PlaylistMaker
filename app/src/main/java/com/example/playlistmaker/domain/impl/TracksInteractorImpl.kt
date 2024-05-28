package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.models.Resource
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.api.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(private val tracksRepository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, tracksConsumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = tracksRepository.searchTracks(expression)) {
                is Resource.Success -> {
                    tracksConsumer.consume(foundTracks = resource.data ?: emptyList(), isFailed = false)
                }
                is Resource.Error -> {
                    tracksConsumer.consume(foundTracks = emptyList(), isFailed = resource.isFailed ?: true)
                }
            }
        }
    }
}
