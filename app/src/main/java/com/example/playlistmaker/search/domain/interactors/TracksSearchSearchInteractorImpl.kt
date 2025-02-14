package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.media.data.db.AppDatabase
import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchRepository
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TracksSearchSearchInteractorImpl(
    private val tracksSearchRepository: TracksSearchRepository,
    private val appDatabase: AppDatabase // Экземпляр базы данных
) : TracksSearchInteractor {

    override fun searchTracks(expression: String): Flow<Pair<List<Track>?, Boolean?>> {
        return tracksSearchRepository.searchTracks(expression).map { result ->
            when (result) {
                is Resource.Success -> {
                    val favoriteIds = appDatabase.favoriteTracksDao().getFavoriteTrackIds().first()
                    val updatedTracks = result.data?.map { track ->
                        track.apply {
                            isFavorite = favoriteIds.contains(trackId)
                        }
                    }
                    Pair(updatedTracks, null)
                }

                is Resource.Error -> {
                    Pair(null, result.isFailed)
                }
            }
        }
    }
}
