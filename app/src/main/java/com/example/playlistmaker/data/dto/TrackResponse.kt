package com.example.playlistmaker.data.dto

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("results") val tracks: List<TrackDto>
) : Response()
