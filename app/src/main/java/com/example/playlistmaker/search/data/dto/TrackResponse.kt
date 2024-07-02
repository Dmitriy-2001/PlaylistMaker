package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.annotations.SerializedName

class TrackResponse(@SerializedName("results") val tracks: ArrayList<Track>): Response() {
}