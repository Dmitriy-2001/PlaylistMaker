package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackRequest

interface NetworkClient {
    fun doRequest(dto: Any): Response
}