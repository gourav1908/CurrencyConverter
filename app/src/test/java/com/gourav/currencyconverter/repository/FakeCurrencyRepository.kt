package com.gourav.currencyconverter.repository

import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.utils.ResponseState
import retrofit2.Response

class FakeCurrencyRepository : RepositoryInterface {
    private val response: Response<CurrencyModel>? = null

    override suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): ResponseState<String> {
        return ResponseState.Success("data")
    }

    override fun checkTimeGap(savedTime: Long, newTime: Long): Boolean {
        val minutesPassed: Long = ((newTime - savedTime) / 1000) / 60 //to minutes
        return minutesPassed > Constants.timeInterval
    }

    override fun performConversion(
        currencyFromAmt: Double,
        currencyToAmt: Double,
        amountToConvert: Double
    ): Double {
        return String.format("%.2f", ((currencyToAmt / currencyFromAmt) * amountToConvert))
            .toDouble()
    }
}