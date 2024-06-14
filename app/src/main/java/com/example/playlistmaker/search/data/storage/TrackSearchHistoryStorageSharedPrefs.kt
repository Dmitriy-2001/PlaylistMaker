package com.example.playlistmaker.search.data.storage

import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.search.data.dto.TrackDto
import com.google.gson.Gson

const val KEY_FOR_HISTORY_LIST = "KEY_FOR_HISTORY_LIST"
const val SHARED_PREFERENCES = "SHARED_PREFERENCES"

class TrackSearchHistoryStorageSharedPrefs (context: Context): TrackSearchHistoryStorage{

    private val sharedPreferences: SharedPreferences? = context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)

    override fun getTracksFromStorage(): Array<TrackDto> {
        val json = sharedPreferences?.getString(KEY_FOR_HISTORY_LIST, null) ?: return emptyArray()
        return Gson().fromJson(json, Array<TrackDto>::class.java)
    }

    override fun saveTracksToStorage(tracks: ArrayList<TrackDto>) {
        val json = Gson().toJson(tracks)
        sharedPreferences?.edit()
            ?.putString(KEY_FOR_HISTORY_LIST, json)
            ?.apply()
    }
}