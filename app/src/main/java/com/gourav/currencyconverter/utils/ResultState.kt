package com.gourav.currencyconverter.utils

import com.gourav.currencyconverter.data.models.Rates

sealed class ResultState<T>(val convertedList: List<Rates>, val message: String?) {
    class Pass<T>(convertedList: List<Rates>) : ResponseState<T>(convertedList, null)

}
