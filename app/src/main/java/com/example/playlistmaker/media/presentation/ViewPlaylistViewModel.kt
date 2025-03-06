package com.example.playlistmaker.media.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.R
import com.example.playlistmaker.media.domain.interactors.PlaylistsInteractor
import com.example.playlistmaker.media.domain.model.Playlist
import com.example.playlistmaker.media.domain.repository.GetPlaylistUseCase
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class ViewPlaylistViewModel(
    application: Application,
    private val getPlaylistUseCase: GetPlaylistUseCase,
    private val playlistInteractor: PlaylistsInteractor,

    ) : AndroidViewModel(application) {

    private val _playlistLiveData = MutableLiveData<Playlist>()
    val playlistLiveData: LiveData<Playlist> get() = _playlistLiveData
    private val _trackTimeLiveData = MutableLiveData<Long>()
    val trackTimeLiveData: LiveData<Long> = _trackTimeLiveData
    private val _trackListLiveData = MutableLiveData<List<Track>>()
    val trackListLiveData: LiveData<List<Track>> = _trackListLiveData
    private val _updatedPlaylistLiveData = MutableLiveData<Playlist>()
    val updatedPlaylistLiveData: LiveData<Playlist> get() = _updatedPlaylistLiveData

    fun getPlaylist(json: String) {
        val playlist = getPlaylistUseCase.execute(json)
        Log.d("ViewPlaylistViewModel", playlist.toString())
        playlist?.let {
            _playlistLiveData.postValue(it)
            viewModelScope.launch {
                playlistInteractor.getTracksInPlaylist(playlist.tracksIdInPlaylist)
                    .collect { tracks ->
                        _trackTimeLiveData.postValue(TimeUnit.MILLISECONDS.toMinutes(tracks.map { it.trackTime }
                            .sum()))
                        _trackListLiveData.postValue(tracks)
                    }
            }
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        _playlistLiveData.postValue(playlist)
        _updatedPlaylistLiveData.postValue(playlist)
        viewModelScope.launch {
            playlistInteractor.getTracksInPlaylist(playlist.tracksIdInPlaylist)
                .collect { tracks ->
                    _trackTimeLiveData.postValue(TimeUnit.MILLISECONDS.toMinutes(tracks.map { it.trackTime }.sum()))
                    _trackListLiveData.postValue(tracks)
                }
        }
    }
    fun refreshPlaylist(playlistId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val updatedPlaylist = playlistInteractor.getPlaylistById(playlistId)
            updatedPlaylist?.let {
                _playlistLiveData.postValue(it)
                playlistInteractor.getTracksInPlaylist(it.tracksIdInPlaylist)
                    .collect { tracks ->
                        _trackTimeLiveData.postValue(TimeUnit.MILLISECONDS.toMinutes(tracks.sumOf { track -> track.trackTime }))
                        _trackListLiveData.postValue(tracks)
                    }
            }
        }
    }

    fun getMessage(): String? {
        val tracks = trackListLiveData.value
        if (tracks.isNullOrEmpty()) return null
        val playlist = playlistLiveData.value
        playlist?.let {
            val sb = StringBuilder(playlist.playlistName)
            sb.append("\n")
            playlist.playlistDescription?.let { sb.appendLine(playlist.playlistDescription) }
            sb.appendLine(
                getApplication<Application>().resources.getQuantityString(
                    R.plurals.tracks,
                    playlist.tracksIdInPlaylist.size,
                    playlist.tracksIdInPlaylist.size
                )
            )
            val PATTERN = "mm:ss"
            val formatter = DateTimeFormatter.ofPattern(PATTERN)
            for ((index, track: Track) in tracks.withIndex()) {
                val localDateTime =
                    Instant.ofEpochMilli(track.trackTime).atZone(ZoneOffset.UTC).toLocalDateTime()

                sb.appendLine("${index + 1}.${track.artistName} - ${track.trackName}(${localDateTime.format(formatter)})")
            }
            return sb.toString()
        }
        return null
    }

    fun deletePlaylist() {
        viewModelScope.launch {
            _playlistLiveData.value?.let {
                playlistInteractor.deletePlaylist(it)
            }
        }
    }

    fun removeTrack(trackId: Int) {
        viewModelScope.launch {
            _playlistLiveData.value?.let {
                playlistInteractor.removeTrack(it, trackId)
            }
        }
    }
}