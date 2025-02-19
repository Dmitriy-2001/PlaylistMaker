package com.example.playlistmaker.search.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.domain.interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.interfaces.TracksSearchInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchingViewModel(
    private val tracksSearchInteractor: TracksSearchInteractor,
    private val trackHistoryInteractor: TrackHistoryInteractor
): ViewModel() {

    private var searchJob: Job? = null

    private val _historyList = MutableLiveData<ArrayList<Track>>()
    val historyList: LiveData<ArrayList<Track>> = _historyList

    init {
        assignListToHistoryList()
    }

    private val _tracksState = MutableStateFlow(TracksState(emptyList(), false, null))
    val tracksState: StateFlow<TracksState> = _tracksState

    private var lastSearchText: String? = null

    private val tracks = ArrayList<Track>()


    private fun assignListToHistoryList() {
        _historyList.postValue(getHistoryList())
    }

    fun getHistoryList(): ArrayList<Track> {
        return trackHistoryInteractor.getHistoryList()
    }

    fun clearHistoryList() {
        trackHistoryInteractor.clearHistoryList()
        _historyList.postValue(getHistoryList())
    }

    fun saveHistoryList() {
        trackHistoryInteractor.saveHistoryList()
    }

    fun addTrackToHistoryList(track: Track) {
        trackHistoryInteractor.addTrackToHistoryList(track)
        _historyList.postValue(getHistoryList())
    }

    fun transferTrackToTop(track: Track) {
        val index = trackHistoryInteractor.transferToTop(track)
        if (index != 0) {
            _historyList.postValue(getHistoryList())
        }
    }

    fun onDestroy() {
        searchJob?.cancel()
    }

    fun searchDebounce(changedText: String) {

        if (lastSearchText == changedText) {
            return
        }

        lastSearchText = changedText

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            searchRequest(lastSearchText!!)
        }

    }

    fun searchRequest(newSearchText: String) {
        if (newSearchText.isNotEmpty()) {

            _tracksState.value =
                TracksState(
                    tracks = null,
                    isLoading = true,
                    isFailed = null
                )


            viewModelScope.launch {
                tracksSearchInteractor
                    .searchTracks(newSearchText)
                    .collect { pair ->
                        if (pair.first != null) {
                            tracks.clear()
                            tracks.addAll(pair.first!!)
                        }
                        if (pair.second != null) {
                            _tracksState.value =
                                TracksState(
                                    tracks = emptyList(),
                                    isLoading = false,
                                    isFailed = pair.second
                                )
                        } else {
                            if (tracks.isEmpty()) {

                                _tracksState.value =
                                    TracksState(
                                        tracks = emptyList(),
                                        isLoading = false,
                                        isFailed = null
                                    )

                            }  else {
                                _tracksState.value =
                                    TracksState(
                                        tracks = tracks,
                                        isLoading = false,
                                        isFailed = null
                                    )
                            }

                        }
                    }
            }
        }
    }
    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}