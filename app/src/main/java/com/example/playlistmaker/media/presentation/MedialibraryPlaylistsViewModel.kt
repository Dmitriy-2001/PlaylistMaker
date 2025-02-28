package com.example.playlistmaker.media.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.ui.PlaylistState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MedialibraryPlaylistsViewModel(private val playlistsInteractor: PlaylistsInteractor): ViewModel(){

    private val stateLiveData = MutableLiveData<PlaylistState>()

    fun observeState(): LiveData<PlaylistState> = stateLiveData

    init {
        getSavedPlaylists()
    }

    private fun getSavedPlaylists() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.getSavedPlaylists().collect { playlists ->
                processResult(playlists)
            }
        }
    }

    private fun processResult(playlists: List<Playlist>) {
        if (playlists.isEmpty()) {
            renderState(PlaylistState.Empty)
        } else {
            renderState(PlaylistState.Content(playlists))
        }
    }

    private fun renderState(state: PlaylistState) {
        stateLiveData.postValue(state)
    }
}