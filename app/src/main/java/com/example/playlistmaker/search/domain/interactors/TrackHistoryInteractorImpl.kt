package com.example.playlistmaker.search.domain.interactors

import com.example.playlistmaker.search.domain.interfaces.HistoryTrackRepositorySH
import com.example.playlistmaker.search.domain.interfaces.TrackHistoryInteractor
import com.example.playlistmaker.search.domain.models.Track

class TrackHistoryInteractorImpl(private val historyTrackRepositorySH: HistoryTrackRepositorySH?):
    TrackHistoryInteractor {

    private val historyList: ArrayList<Track> = ArrayList(historyTrackRepositorySH?.getTrackListFromSH()?.toList())

    override fun getHistoryList(): ArrayList<Track> {
        return historyList
    }

    override fun saveHistoryList() {
        historyTrackRepositorySH?.saveTrackListToSH(historyList)
    }

    override fun addTrackToHistoryList(track: Track) {
        val index = historyList.indexOfFirst { it.trackId == track.trackId }

        if (historyList.size < 10) {
            if (index == -1) {
                historyList.add(0, track)
            } else {
                shiftElementToTopOfHistoryList(index)
            }
        } else {
            if (index == -1) {
                historyList.removeAt(historyList.lastIndex)
                historyList.add(0, track)
            } else {
                shiftElementToTopOfHistoryList(index)
            }
        }
    }

    override fun clearHistoryList() {
        historyList.clear()
    }

    override fun transferToTop(track: Track): Int {
        val index = historyList.indexOfFirst { it.trackId == track.trackId }
        if (index != 0) {
            shiftElementToTopOfHistoryList(index)
        }
        return index
    }

    private fun shiftElementToTopOfHistoryList(index: Int) {
        val trackToMove = historyList[index]
        historyList.removeAt(index)
        historyList.add(0, trackToMove)
    }

}