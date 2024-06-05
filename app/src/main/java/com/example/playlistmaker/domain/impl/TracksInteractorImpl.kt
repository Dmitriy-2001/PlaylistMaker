package com.example.playlistmaker.domain.api

import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val result = repository.searchTracks(expression)
                withContext(Dispatchers.Main) {
                    if (result is Resource.Success) {
                        consumer.consume(result.data, false)
                    } else {
                        consumer.consume(emptyList(), true)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    consumer.consume(emptyList(), true)
                }
            }
        }
    }
}

