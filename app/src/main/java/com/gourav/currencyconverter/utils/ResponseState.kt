package com.gourav.currencyconverter.utils

import com.gourav.currencyconverter.data.models.Rates

sealed class ResponseState<T>(val convertedList: List<Rates>, val message: String?) {
    class Success<T>(convertedList: List<Rates>) : ResponseState<T>(convertedList, null)
    class Failure<T>(message: String) : ResponseState<T>(ArrayList(), message)
}