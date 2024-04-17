package com.example.playlistmaker
import com.google.gson.annotations.SerializedName

class TrackResponse(@SerializedName("results") val tracks: ArrayList<Track>)