package com.gourav.currencyconverter.repository

import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.utils.Resource

interface RepositoryInterface {
//    suspend fun getRates(apiKey: String, base: String): Resource<CurrencyModel>

    suspend fun getResult(fromCurrency: String, toCurrency: String, amount: Double): Resource<String>

    suspend fun saveAndGetRates(result: CurrencyModel, timeStamp: Long): Rates

    fun checkTimeGap(savedTime: Long, newTime: Long): Boolean

    fun performConversion(
        currencyFromAmt: Double,
        currencyToAmt: Double,
        amountToConvert: Double
    ): Double

}