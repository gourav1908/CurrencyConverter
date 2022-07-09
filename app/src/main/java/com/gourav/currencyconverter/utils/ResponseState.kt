package com.gourav.currencyconverter.utils

sealed class ResponseState<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : ResponseState<T>(data, null)
    class Failure<T>(message: String) : ResponseState<T>(null, message)
}