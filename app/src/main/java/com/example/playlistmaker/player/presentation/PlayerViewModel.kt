package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.db.TrackEntity
import com.example.playlistmaker.media.domain.FavoriteTracksRepository
import com.example.playlistmaker.player.domain.PlayerTrack
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

private const val REFRESH_PROGRESS_DELAY_MILLIS = 300L

class PlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksRepository: FavoriteTracksRepository,
    track: Track?
) : ViewModel() {

    private var timerJob: Job? = null

    private val _playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    val playerStateLiveData: LiveData<PlayerState> get() = _playerStateLiveData

    private val _currentTimeLiveData = MutableLiveData<String>()
    val currentTimeLiveData: LiveData<String> get() = _currentTimeLiveData

    private val _trackLiveData = MutableLiveData<Track?>()
    val trackLiveData: LiveData<Track?> get() = _trackLiveData

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

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
    fun changeStatePlayerAfterClick() {
        when (_playerStateLiveData.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Paused, is PlayerState.Prepared -> start()
            else -> {}
        }
    }

    // Работа с избранными треками
    fun toggleFavorite() {
        _trackLiveData.value?.let { track ->
            val playerTrack = track.toPlayerTrack()
            viewModelScope.launch {
                playerTrack.isFavorite = !playerTrack.isFavorite
                if (playerTrack.isFavorite) {
                    favoriteTracksRepository.addTrack(playerTrack.toEntity())
                } else {
                    favoriteTracksRepository.removeTrack(playerTrack.toEntity())
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
                    val formattedTime = dateFormat.format(audioPlayerInteractor.currentPosition())
                    _currentTimeLiveData.postValue(formattedTime)
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
        stopTimer()
        _playerStateLiveData.postValue(PlayerState.Paused(audioPlayerInteractor.currentPosition()))
    }

    private fun setOnPreparedListener(listener: MediaPlayer.OnPreparedListener) {
        audioPlayerInteractor.setOnPreparedListener(listener)
    }

    private fun setOnCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        audioPlayerInteractor.setOnCompletionListener(listener)
    }

    private fun isPlaying(): Boolean {
        return audioPlayerInteractor.isPlaying()
    }

    private fun release() {
        audioPlayerInteractor.release()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}

fun PlayerTrack.toEntity() = TrackEntity(
    trackId = this.id,
    coverUrl = this.artworkUrl100,
    trackName = this.name,
    artistName = this.artistName,
    albumName = this.collectionName,
    releaseYear = this.releaseDate,
    genre = this.primaryGenreName,
    country = this.country,
    duration = this.timeMillis.toString(),
    trackUrl = this.previewUrl
)

fun Track.toPlayerTrack() = PlayerTrack(
    id = this.trackId.toString(),
    name = this.trackName.orEmpty(),
    artistName = this.artistName.orEmpty(),
    timeMillis = this.trackTime?.toLongOrNull() ?: 0L,
    collectionName = this.collectionName.orEmpty(),
    releaseDate = this.releaseDate.orEmpty(),
    primaryGenreName = this.primaryGenreName.orEmpty(),
    country = this.country.orEmpty(),
    artworkUrl100 = this.artworkUrl100.orEmpty(),
    previewUrl = this.previewUrl.orEmpty(),
    isFavorite = this.isFavorite
)
