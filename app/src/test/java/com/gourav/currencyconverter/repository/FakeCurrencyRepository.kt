package com.gourav.currencyconverter.repository

import com.google.gson.JsonElement
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.utils.ResponseState
import junit.framework.TestCase
import org.json.JSONObject
import retrofit2.Response
import java.math.BigDecimal

class FakeCurrencyRepository : RepositoryInterface, TestCase() {
    private val response: Response<JsonElement>? = null
    private val currencyModel: CurrencyModel? = null

    private val conversionsList = mutableListOf<Rates>()
    private var shouldReturnNetworkError = false

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: BigDecimal
    ): ResponseState<String> {

        return ResponseState.Success(conversionsList)
    }

    fun checkTimeGap(savedTime: Long, newTime: Long): Boolean {
        val minutesPassed: Long = ((newTime - savedTime) / 1000) / 60 //to minutes
        return minutesPassed > Constants.timeInterval
    }

    fun performConversions(
        rateList: List<Rates>,
        from: String,
        amt: BigDecimal
    ): MutableList<Rates> {
        val newList = mutableListOf<Rates>()
        rateList.map { rates ->
            newList.add(
                Rates(
                    rates.currencyName,
                    String.format(
                        "%.3f",
                        (rates.amount * amt) / rateList.single { it.currencyName == from }.amount
                    ).toBigDecimal()
                )
            )
        }
        return newList
    }

    override suspend fun saveAndGetRates(result: CurrencyModel): List<Rates> {
        TODO("Not yet implemented")
    }

    fun getCurrencyModel(jsonObject: JSONObject, timeStamp: Long): CurrencyModel {
        TODO("Not yet implemented")
    }
}