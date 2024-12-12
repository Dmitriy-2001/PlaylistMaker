package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.media.domain.FavoriteTracksRepository

class FavoriteTracksViewModel(val repository: FavoriteTracksRepository) : ViewModel() {
    val favoriteTracksLiveData: LiveData<List<TrackEntity>> = repository.getFavoriteTracks().asLiveData()
}