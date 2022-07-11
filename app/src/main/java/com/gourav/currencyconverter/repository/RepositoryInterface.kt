package com.gourav.currencyconverter.repository

import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.utils.ResponseState
import java.math.BigDecimal

interface RepositoryInterface {

    suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: BigDecimal
    ): ResponseState<String>

    suspend fun saveAndGetRates(result: CurrencyModel): List<Rates>
}