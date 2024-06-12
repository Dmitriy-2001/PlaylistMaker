package com.example.playlistmaker.search.data.dto

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("results") val tracks: List<TrackDto>
) : Response()
