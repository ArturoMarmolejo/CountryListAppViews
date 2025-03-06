package com.arturomarmolejo.countrylistappviews.core

sealed class UIState<out T> {
    data object LOADING: UIState<Nothing>()
    data class SUCCESS<T>(val response: T): UIState<T>()
    data class ERROR(val message: Exception): UIState<Nothing>()
}