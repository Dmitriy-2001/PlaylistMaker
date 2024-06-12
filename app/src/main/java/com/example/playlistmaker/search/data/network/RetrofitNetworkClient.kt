package com.example.playlistmaker.search.data.network

import android.content.Context
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient(private val context: Context): NetworkClient {

    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(ItunesApi::class.java)
    private val networkUtils = NetworkUtils(context)

    override suspend fun doRequest(dto: Any): Response {
        if (!networkUtils.isConnected()) {
            return Response().apply { resultCode = -1 }
        }
        if (dto !is TrackRequest) {
            return Response().apply { resultCode = 400 }
        }

        val response = trackService.search(dto.expression).execute()

        val body = response.body()

        return body?.apply { resultCode = response.code() } ?: Response().apply { resultCode = response.code() }
    }
}
