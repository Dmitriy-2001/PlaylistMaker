package com.example.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.interfaces.AudioPlayerInteractor
import com.example.playlistmaker.player.domain.models.PlayerTrack
import java.text.SimpleDateFormat
import java.util.Locale

const val STATE_DEFAULT = 0
const val STATE_PREPARED = 1
const val STATE_PLAYING = 2
const val STATE_PAUSED = 3

class PlayerViewModel(
    private val playerTrack: PlayerTrack,
    private val audioPlayerInteractor: AudioPlayerInteractor
): ViewModel() {

    private val _playerTrack = MutableLiveData<PlayerTrack>()
    val playerTrackForRender: LiveData<PlayerTrack> = _playerTrack


    private val mainThreadHandler = Handler(Looper.getMainLooper())

    private val _isCompleted = MutableLiveData(false)
    val isCompleted: LiveData<Boolean> = _isCompleted

    private val _playerState = MutableLiveData(STATE_DEFAULT)
    val playerState: LiveData<Int> = _playerState

    private val _formattedTime =  MutableLiveData("00:00")
    val formattedTime: LiveData<String> = _formattedTime
    private val cycleRunnable = object: Runnable {
        override fun run() {
            _formattedTime.postValue(getTimeFormat(audioPlayerInteractor.getCurrentPos().toLong()))
            mainThreadHandler.postDelayed(this, UPDATE_TIME_INFO_MS)
        }
    }

    init {
        preparePlayer(playerTrack)
        assignValToPlayerTrackForRender()
    }

    private fun assignValToPlayerTrackForRender() {
        val playerTrackTo = playerTrack.copy(
            artworkUrl = playerTrack.artworkUrl.replaceAfterLast('/', "512x512bb.jpg"),
            releaseDate = playerTrack.releaseDate.split("-", limit = 2)[0],
            trackTime = getTimeFormat(playerTrack.trackTime.toLong())
        )
        _playerTrack.postValue(playerTrackTo)
    }

    fun play() {
        audioPlayerInteractor.play()
        _playerState.postValue(STATE_PLAYING)
        mainThreadHandler.postDelayed(cycleRunnable, UPDATE_TIME_INFO_MS)
        _isCompleted.postValue(false)
    }

    fun pause() {
        audioPlayerInteractor.pause()
        _playerState.postValue(STATE_PAUSED)
        mainThreadHandler.removeCallbacks(cycleRunnable)
    }

    fun release() {
        audioPlayerInteractor.release()
        mainThreadHandler.removeCallbacks(cycleRunnable)
    }

    fun playbackControl() {
        when (_playerState.value) {
            STATE_PLAYING -> pause()
            STATE_PAUSED, STATE_PREPARED -> play()
        }
    }

    private fun preparePlayer(playerTrack: PlayerTrack) {
        audioPlayerInteractor.prepare(
            playerTrack = playerTrack,
            callbackPrep = {
                _playerState.postValue(STATE_PREPARED)
            },
            callbackComp = {
                _playerState.postValue(STATE_PREPARED)
                mainThreadHandler.removeCallbacks(cycleRunnable)
                _isCompleted.postValue(true)
            }
        )
    }

    fun getTimeFormat(value: Long): String =
        SimpleDateFormat("mm:ss", Locale.getDefault()).format(value)

    companion object {
        private const val UPDATE_TIME_INFO_MS = 300L
    }
}