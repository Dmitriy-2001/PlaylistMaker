package com.example.playlistmaker.utils

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

sealed class Resource<T>(val data: T? = null, val isFailed: Boolean? = null) {
    class Success<T>(data: T): Resource<T>(data), Flow<Resource<List<Track>>> {
        override suspend fun collect(collector: FlowCollector<Resource<List<Track>>>) {
            TODO("Not yet implemented")
        }
    }

    class Error<T>(isFailed: Boolean, data: T? = null): Resource<T>(data, isFailed),
        Flow<Resource<List<Track>>> {
        override suspend fun collect(collector: FlowCollector<Resource<List<Track>>>) {
            TODO("Not yet implemented")
        }
    }
}