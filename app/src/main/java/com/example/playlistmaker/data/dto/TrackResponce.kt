package com.example.playlistmaker.data.dto

import com.example.playlistmaker.domain.models.Track
import com.google.gson.annotations.SerializedName

class TrackResponse(@SerializedName("results") val tracks: ArrayList<Track>): Response() {
}
