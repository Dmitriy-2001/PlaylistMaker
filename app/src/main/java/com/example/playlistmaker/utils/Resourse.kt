package com.example.playlistmaker.utils

sealed class Resource<T>(val data: T? = null, val isFailed: Boolean? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(isFailed: Boolean, data: T? = null): Resource<T>(data, isFailed)
}