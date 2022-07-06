package com.gourav.currencyconverter.repository

import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.utils.Resource

interface RepositoryInterface {
    suspend fun getRates(apiKey: String, base: String): Resource<CurrencyModel>
}