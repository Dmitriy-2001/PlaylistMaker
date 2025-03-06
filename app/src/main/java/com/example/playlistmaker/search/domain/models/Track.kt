package com.example.playlistmaker.search.domain.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import java.io.Serializable
import kotlinx.parcelize.Parcelize
@Parcelize
data class Track(
    val trackId: Int,
    val trackName: String?,
    val artistName: String?,
    @SerializedName("trackTimeMillis") val trackTime: Long,
    @SerializedName("artworkUrl100") val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?,
    var isFavorite: Boolean = false
) : Parcelable {
    val artworkUrl512: String?
        get() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
}