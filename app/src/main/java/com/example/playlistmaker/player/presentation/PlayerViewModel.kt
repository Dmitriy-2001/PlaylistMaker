package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val REFRESH_PROGRESS_DELAY_MILLIS = 300L

class PlayerViewModel(

    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksRepository: FavoriteTracksRepository,
    track: Track?
): ViewModel() {

    private var timerJob: Job? = null

    private val _playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerStateLiveData: LiveData<PlayerState> get() = _playerStateLiveData

    private val _currentTimeLiveData = MutableLiveData<String>()
    val currentTimeLiveData: LiveData<String> get() = _currentTimeLiveData

    private val _trackLiveData = MutableLiveData<Track?>()
    val trackLiveData: LiveData<Track?> get() = _trackLiveData

    init {
        track?.let {
            viewModelScope.launch {
                it.isFavorite = isTrackFavorite(it.trackId.toString())
                _trackLiveData.postValue(it)
            }
        }
        setDataSource(track?.previewUrl)
        preparePlayer()
        setOnPreparedListener {
            _playerStateLiveData.postValue(PlayerState.Prepared())
        }
        setOnCompletionListener {
            _playerStateLiveData.postValue(PlayerState.Prepared())
        }
    }
    // Изменение состояния плеера после клика Play
    fun changeStatePlayerAfterClick () {
        when (_playerStateLiveData.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Paused, is PlayerState.Prepared -> start()
            else -> {}
        }
    }

    // Работа с избранными треками
    fun toggleFavorite() {
        _trackLiveData.value?.let { track ->
            viewModelScope.launch {
                track.isFavorite = !track.isFavorite
                if (track.isFavorite) {
                    favoriteTracksRepository.addTrack(track.toEntity())
                } else {
                    favoriteTracksRepository.removeTrack(track.toEntity())
                }
                _trackLiveData.postValue(track)
            }
        }
    }

    private suspend fun isTrackFavorite(trackId: String): Boolean {
        val favoriteTrackIds = favoriteTracksRepository.getAllFavoriteTrackIds()
        return favoriteTrackIds.contains(trackId)
    }

    // Таймер обновления прогресса
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (isPlaying()) {
                delay(REFRESH_PROGRESS_DELAY_MILLIS)
                if (_playerStateLiveData.value is PlayerState.Playing) {
                    _currentTimeLiveData.postValue(audioPlayerInteractor.currentPosition())
                }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
    }

    // Плеер
    private fun setDataSource(url: String?) {
        audioPlayerInteractor.setDataSource(url)
    }
    private fun preparePlayer() {
        audioPlayerInteractor.preparePlayer()
    }
    private fun start() {
        audioPlayerInteractor.start()
        _playerStateLiveData.postValue(PlayerState.Playing(audioPlayerInteractor.currentPosition()))
        startTimer()
    }
    fun pause() {
        audioPlayerInteractor.pause()
        timerJob?.cancel()
        _playerStateLiveData.postValue(PlayerState.Paused(audioPlayerInteractor.currentPosition()))
    }



    private fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        audioPlayerInteractor.setOnPreparedListener(listener)
    }
    private fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        audioPlayerInteractor.setOnCompletionListener(listener)
    }
    private fun isPlaying (): Boolean {
        return audioPlayerInteractor.isPlaying()
    }
    private fun release () {
        audioPlayerInteractor.release()
    }
    override fun onCleared() {
        super.onCleared()
        release()
    }
}
// Расширение для преобразования Track в TrackEntity
fun Track.toEntity() = TrackEntity(
    trackId = this.trackId.toString(),
    coverUrl = this.artworkUrl100.orEmpty(),
    trackName = this.trackName.orEmpty(),
    artistName = this.artistName.orEmpty(),
    albumName = this.collectionName.orEmpty(),
    releaseYear = this.releaseDate.orEmpty(),
    genre = this.primaryGenreName.orEmpty(),
    country = this.country.orEmpty(),
    duration = this.trackTime ?: "0",
    trackUrl = this.previewUrl.orEmpty()
)