package com.example.playlistmaker.search.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.search.domain.interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TracksState

class SearchingViewModel(
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
): ViewModel() {

    private val _historyList = MutableLiveData<ArrayList<Track>>()
    val historyList: LiveData<ArrayList<Track>> = _historyList

    init {
        assignListToHistoryList()
    }

    private val _tracksState = MutableLiveData<TracksState>()
    val tracksState: LiveData<TracksState> = _tracksState

    private var lastSearchText: String? = null

    private val tracks = ArrayList<Track>()

    private val handler = Handler(Looper.getMainLooper())

    private val searchRunnable = Runnable {
        val newSearchText = lastSearchText ?: ""
        searchRequest(newSearchText)
    }

    private fun assignListToHistoryList() {
        val hList = getHistoryList()
        _historyList.postValue(hList)
    }

    fun getHistoryList(): ArrayList<Track> {
        return trackHistoryInteractor.getHistoryList()
    }

    fun clearHistoryList() {
        trackHistoryInteractor.clearHistoryList()
        val hList = getHistoryList()
        _historyList.postValue(hList)
    }

    fun saveHistoryList() {
        trackHistoryInteractor.saveHistoryList()
    }

    fun addTrackToHistoryList(track: Track) {
        trackHistoryInteractor.addTrackToHistoryList(track)
        val hList = getHistoryList()
        _historyList.postValue(hList)
    }

    fun transferTrackToTop(track: Track) {
        val index = trackHistoryInteractor.transferToTop(track)
        if (index != 0) {
            _historyList.postValue(getHistoryList())
        }
    }

    fun onDestroy() {
        handler.removeCallbacks(searchRunnable)
    }

    fun searchDebounce(changedText: String) {

        this.lastSearchText = changedText
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            _tracksState.postValue(
                TracksState(
                    tracks = tracks,
                    isLoading = true,
                    isFailed = null
                )
            )

            tracksSearchInteractor.searchTracks(
                newSearchText,
                object : TracksSearchInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, isFailed: Boolean?) {

                        handler.post {

                            if (foundTracks != null) {
                                tracks.clear()
                                tracks.addAll(foundTracks)
                            }


                            if (isFailed != null) {

                                _tracksState.postValue(
                                    TracksState(
                                        tracks = emptyList(),
                                        isLoading = false,
                                        isFailed = isFailed
                                    )
                                )

                            } else {

                                if (tracks.isEmpty()) {
                                    _tracksState.postValue(
                                        TracksState(
                                            tracks = emptyList(),
                                            isLoading = false,
                                            isFailed = null
                                        )
                                    )
                                } else {
                                    _tracksState.postValue(
                                        TracksState(
                                            tracks = tracks,
                                            isLoading = false,
                                            isFailed = null
                                        )
                                    )
                                }

                            }
                        }
                    }
                }
            )
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}