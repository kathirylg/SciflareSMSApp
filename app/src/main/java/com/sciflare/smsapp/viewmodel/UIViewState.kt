package com.sciflare.smsapp.viewmodel

sealed interface UIViewState<out T> {

    data class Success<T>(val data: T) : UIViewState<T>
    data class Update<T>(val data: T) : UIViewState<T>

    data class Error(val messages: String) : UIViewState<Nothing>

    object Loading : UIViewState<Nothing>
}