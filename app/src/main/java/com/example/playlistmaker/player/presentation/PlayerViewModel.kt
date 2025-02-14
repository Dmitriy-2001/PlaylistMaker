package com.example.playlistmaker.player.presentation

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.media.domain.interactors.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val REFRESH_PROGRESS_DELAY = 300L

class PlayerViewModel(
    private val audioPlayerInteractor: AudioPlayerInteractor,
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val track: Track? // Передаем текущий трек
) : ViewModel() {

    private var timerJob: Job? = null

    private val statePlayerLiveData = MutableLiveData<PlayerState>(PlayerState.Default())
    private val isFavoriteLiveData = MutableLiveData<Boolean>(track?.isFavorite ?: false)

    // Получение состояния плеера
    fun getStatePlayerLiveData(): LiveData<PlayerState> = statePlayerLiveData
    fun getIsFavoriteLiveData(): LiveData<Boolean> = isFavoriteLiveData

    init {
        setDataSource(track?.previewUrl)
        preparePlayer()
        setOnPreparedListener {
            statePlayerLiveData.postValue(PlayerState.Prepared())
        }
        setOnCompletionListener {
            statePlayerLiveData.postValue(PlayerState.Prepared())
        }
    }

    // Обработка клика по кнопке "Нравится"
    fun onFavoriteClicked() {
        track?.let {
            viewModelScope.launch {
                if (it.isFavorite) {
                    favoriteTracksInteractor.removeTrackFromFavorites(it)
                } else {
                    favoriteTracksInteractor.addTrackToFavorites(it)
                }
                it.isFavorite = !it.isFavorite
                isFavoriteLiveData.postValue(it.isFavorite)
            }
        }
    }

    // Изменение состояния плеера после клика Play
    fun changeStatePlayerAfterClick() {
        when (statePlayerLiveData.value) {
            is PlayerState.Playing -> pause()
            is PlayerState.Paused, is PlayerState.Prepared -> start()
            else -> {}
        }
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
        statePlayerLiveData.postValue(PlayerState.Playing(currentPosition()))
        startTimer()
    }

    fun pause() {
        audioPlayerInteractor.pause()
        timerJob?.cancel()
        statePlayerLiveData.postValue(PlayerState.Paused(currentPosition()))
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (isPlaying()) {
                delay(REFRESH_PROGRESS_DELAY)
                if (statePlayerLiveData.value is PlayerState.Playing) {
                    statePlayerLiveData.postValue(PlayerState.Playing(currentPosition()))
                }
            }
        }
    }

    private fun currentPosition(): String {
        return audioPlayerInteractor.currentPosition()
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
