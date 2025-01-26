package com.example.playlistmaker.media.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.repository.FavoriteTracksRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedialibraryFavouritesViewModel(
    private val favoriteTracksRepository: FavoriteTracksRepository
) : ViewModel() {

    private val _favoriteTracks = MutableStateFlow<List<Track>>(emptyList())
    val favoriteTracks: StateFlow<List<Track>> = _favoriteTracks

    init {
        loadFavoriteTracks()
    }

    private fun loadFavoriteTracks() {
        viewModelScope.launch {
            favoriteTracksRepository.getFavoriteTracks().collect { tracks ->
                _favoriteTracks.value = tracks
            }
        }
    }
}
