package com.example.playlistmaker.media.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactors.LocalStorageInteractor
import com.example.playlistmaker.media.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

open class NewPlaylistViewModel(
    private val localStorageInteractor: LocalStorageInteractor,
    private val playlistsInteractor: PlaylistsInteractor
) :
    ViewModel() {

    private var playlistName = ""
    private var playlistDescription: String? = null
    private var uri: String? = null

    fun saveImageToLocalStorage(uri: Uri): Uri {
        return localStorageInteractor.saveImageToLocalStorage(uri)
    }

    fun createPlaylist() {
        viewModelScope.launch(Dispatchers.IO) {
            playlistsInteractor.createPlaylist(
                Playlist(
                    playlistId = 0,
                    playlistName = playlistName,
                    playlistDescription = playlistDescription,
                    uri = uri,
                    tracksIdInPlaylist = emptyList(),
                    tracksCount = 0
                )
            )
        }
    }

    open fun setPlaylistName(playlistName: String) {
        this.playlistName = playlistName
    }

    open fun setPlaylistDescroption(playlistDescription: String) {
        this.playlistDescription = playlistDescription
    }

    open fun setUri(uri: Uri?) {
        this.uri = uri?.toString()
    }
}