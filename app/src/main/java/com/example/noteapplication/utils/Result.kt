package com.example.noteapplication.utils

sealed class Results<out T : Any> {
    data class Success<out T : Any>(val data: T) : Results<T>()
    data class Failure(val exception: Exception) : Results<Nothing>()
}