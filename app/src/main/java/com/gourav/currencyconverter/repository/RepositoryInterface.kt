package com.gourav.currencyconverter.repository

import com.gourav.currencyconverter.utils.ResponseState

interface RepositoryInterface {

    suspend fun getResult(fromCurrency: String, toCurrency: String, amount: Double): ResponseState<String>

    fun checkTimeGap(savedTime: Long, newTime: Long): Boolean

    fun performConversion(
        currencyFromAmt: Double,
        currencyToAmt: Double,
        amountToConvert: Double
    ): Double
}