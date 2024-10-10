package com.example.playlistmaker.search.domain.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Track(
    val trackId: Int, // ID трека
    val trackName: String?, // Название композиции
    val artistName: String?, // Имя исполнителя
    @SerializedName("trackTimeMillis") val trackTime: String?, // Продолжительность трека в миллисекундах
    @SerializedName("artworkUrl100") val artworkUrl100: String?, // Ссылка на изображение обложки (Малый)
    val collectionName: String?, // Название альбома
    val releaseDate: String?, // Год релиза трека
    val primaryGenreName: String?, // Жанр трека
    val country: String?, // Страна исполнителя
    val previewUrl: String? // URL отрывка трека
) : Serializable {
    val artworkUrl512: String? // Ссылка на изображение обложки (Большой)
        get() = artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg") // Генерация ссылки на большую обложку
}
