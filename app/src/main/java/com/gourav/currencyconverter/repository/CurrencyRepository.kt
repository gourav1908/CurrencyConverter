package com.gourav.currencyconverter.repository

import android.content.Context
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.data.network.ApiInterface
import com.gourav.currencyconverter.data.room.AppDAO
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.utils.NetworkUtils
import com.gourav.currencyconverter.utils.Resource
import retrofit2.Response
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val api: ApiInterface, private val appDAO: AppDAO, private val context: Context
) {
    suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Resource<String> {
        try {
            val response: Response<CurrencyModel>?
            val result: CurrencyModel?
            if (appDAO.getRates() != null) {
                //compare saved time in room with current time(if > 30mins)
                if (checkTimeGap(appDAO.getSavedTime(), System.currentTimeMillis())) {
                    // time > 30
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        /*--network available--*/
                        //hit API, add to room, add timestamp, convert
                        response = api.getRates(Constants.apikey, "USD") // hit API
                        result = response.body()
                        if (response.isSuccessful && result != null) {
                            val time = System.currentTimeMillis()
                            val savedRates = saveAndGetRates(result, time)
                            val finalAmount = performConversion(
                                getRateForCurrency(fromCurrency, savedRates)!!,
                                getRateForCurrency(toCurrency, savedRates)!!, amount
                            )
                            return Resource.Success(finalAmount.toString())
                        } else {
                            return Resource.Failure(response.message())
                        }
                    } else {
                        val savedRates = appDAO.getRates().rates
                        val finalAmount = performConversion(
                            getRateForCurrency(fromCurrency, savedRates)!!,
                            getRateForCurrency(toCurrency, savedRates)!!, amount
                        )
                        return Resource.Success(finalAmount.toString())
                    }
                } else {
                    //time <= 30m, convert
                    val savedRates = appDAO.getRates().rates
                    val finalAmount = performConversion(
                        getRateForCurrency(fromCurrency, savedRates)!!,
                        getRateForCurrency(toCurrency, savedRates)!!, amount
                    )
                    return Resource.Success(finalAmount.toString())
                }

            } else {
                //no data in room already
                if (NetworkUtils.isNetworkAvailable(context)) {
                    /*--network available--*/
                    //hit API, add to room, add timestamp, convert
                    response = api.getRates(Constants.apikey, Constants.baseCurrency)
                    result = response.body()
                    if (response.isSuccessful && result != null) {
                        val time = System.currentTimeMillis()
                        val savedRates = saveAndGetRates(result, time)
                        val finalAmount = performConversion(
                            getRateForCurrency(fromCurrency, savedRates)!!,
                            getRateForCurrency(toCurrency, savedRates)!!, amount
                        )
                        return Resource.Success(finalAmount.toString())
                    } else {
                        return Resource.Failure(response.message())
                    }
                } else {
                    //no network
                    return Resource.Failure("Please connect to the internet!!")
                }
            }
        } catch (e: Exception) {
            //catch exception
            return Resource.Failure(e.message ?: "error occured")
        }
    }

    suspend fun saveAndGetRates(result: CurrencyModel, timeStamp: Long): Rates {
        appDAO.insertRates(result)
        appDAO.addTimeStamp(timeStamp)
        return appDAO.getRates().rates
    }

    fun checkTimeGap(savedTime: Long, newTime: Long): Boolean {
        val minutesPassed: Long
        minutesPassed = ((newTime - savedTime) / 1000) / 60 //minutes
        return minutesPassed > 4
    }

    fun performConversion(
        currencyFromAmt: Double,
        currencyToAmt: Double,
        amountToConvert: Double
    ): Double {
        return String.format("%.2f", ((currencyToAmt / currencyFromAmt) * amountToConvert))
            .toDouble()
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "CAD" -> rates.cAD
        "HKD" -> rates.hKD
        "ISK" -> rates.iSK
        "EUR" -> rates.eUR
        "PHP" -> rates.pHP
        "DKK" -> rates.dKK
        "HUF" -> rates.hUF
        "CZK" -> rates.cZK
        "AED" -> rates.aED
        "AUD" -> rates.aUD
        "RON" -> rates.rON
        "SEK" -> rates.sEK
        "IDR" -> rates.iDR
        "INR" -> rates.iNR
        "BRL" -> rates.bRL
        "RUB" -> rates.rUB
        "HRK" -> rates.hRK
        "JPY" -> rates.jPY
        "THB" -> rates.tHB
        "CHF" -> rates.cHF
        "SGD" -> rates.sGD
        "PLN" -> rates.pLN
        "BGN" -> rates.bGN
        "CNY" -> rates.cNY
        "NOK" -> rates.nOK
        "NZD" -> rates.nZD
        "ZAR" -> rates.zAR
        "USD" -> rates.uSD
        "MXN" -> rates.mXN
        "ILS" -> rates.iLS
        "GBP" -> rates.gBP
        "KRW" -> rates.kRW
        "MYR" -> rates.mYR
        else -> null
    }
}