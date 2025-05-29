package com.example.playlistmaker.media.domain.interactors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoriteTracksInteractor(private val repository: FavoriteTracksRepository) {

    private val _favoriteStateLiveData = MutableLiveData<Pair<Int, Boolean>>()
    val favoriteStateLiveData: LiveData<Pair<Int, Boolean>> get() = _favoriteStateLiveData

    fun getFavoriteTracks(): Flow<List<Track>> {
        return repository.getFavoriteTracks()
    }

    suspend fun addTrackToFavorites(track: Track) {
        repository.addTrackToFavorites(track)
        _favoriteStateLiveData.postValue(track.trackId to true)
    }

    suspend fun removeTrackFromFavorites(track: Track) {
        repository.removeTrackFromFavorites(track)
        _favoriteStateLiveData.postValue(track.trackId to false)
    }

    fun getFavoriteTrackIds(): Flow<List<Int>> {
        return repository.getFavoriteTrackIds()
    }
}
