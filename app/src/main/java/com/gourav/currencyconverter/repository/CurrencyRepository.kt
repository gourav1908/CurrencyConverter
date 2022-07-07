package com.gourav.currencyconverter.repository

import android.content.Context
import android.util.Log
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

    private val TAG: String? = "repo>>>>>"

    suspend fun convertCurrency(apiKey: String, base: String): Resource<CurrencyModel?> {
        try {
            val response: Response<CurrencyModel>?
            val result: CurrencyModel?
            var toCurrencyRate = 0.0
            val fromCurrencyRate = 0.0
            if (appDAO.getRates() != null) {
                Log.e(TAG, "convertCurrency: 1. room not null")
                //compare saved time in room with current time(if > 30mins)
                if (checkTimeGap(appDAO.getSavedTime(), System.currentTimeMillis())) {
                    // time > 30
                    return if (NetworkUtils.isNetworkAvailable(context)) {
                        /*--network available--*/
                        //hit API
                        //add/update to room
                        //add/update timestamp
                        response = api.getRates(apiKey, base) // hit API
                        result = response.body()
//                        toCurrencyRate = getRateForCurrency(base, result!!.rates).toDouble()

                        if (response.isSuccessful && result != null) {
                            appDAO.deletePreviousData()
                            appDAO.insertRates(result)
                            appDAO.addTimeStamp(System.currentTimeMillis())
                            Resource.Success(result)
                        } else {
                            Resource.Failure(response.message())
                        }

                    } else {
                        //display from room
                        //show additional warning to update old data
                        Resource.Success(appDAO.getRates())
                    }
                } else {
                    //time <= 30 mins
                    //display only
                    //no room saving
                    //no timeupdate
                    return Resource.Success(appDAO.getRates())
                }

            } else {
                //no data in room already
                return if (NetworkUtils.isNetworkAvailable(context)) {
                    /*--network available--*/
                    //hit API
                    //add to room
                    //add timestamp
                    response = api.getRates(apiKey, base) // hit API
                    result = response.body()
                    if (response.isSuccessful && result != null) {
                        val time = System.currentTimeMillis()
                        Log.e(TAG, "convertCurrency: time: $time")
                        appDAO.insertRates(result)
                        appDAO.addTimeStamp(time)
                        Resource.Success(result)
                    } else {
                        Resource.Failure(response.message())
                    }
                } else {
                    //no network
                    Resource.Failure("Please connect to the internet!!")
                }
            }
        } catch (e: Exception) {
            return Resource.Failure(e.message ?: "error occured")
        }
    }

    suspend fun getData(apiKey: String, base: String): Resource<CurrencyModel> {
        try {
            val response: Response<CurrencyModel>?
            val result: CurrencyModel?
            if (NetworkUtils.isNetworkAvailable(context)) {
                response = api.getRates(apiKey, base)
                result = response.body()
                appDAO.insertRates(result) // saving in room
                appDAO.addTimeStamp(System.currentTimeMillis()) // updating timestamp value
                return if (response.isSuccessful && result != null) {
                    Log.e(TAG, "getData: from remote")
                    Resource.Success(result)
                } else {
                    Resource.Failure(response.message())
                }
            } else {
                //retrieving from local
                result = appDAO.getRates()
                return if (result != null) {
                    Log.e(TAG, "getData: from room")
                    Resource.Success(result)
                } else {
                    Resource.Failure("Please connect to the internet!!")
                }
            }
        } catch (e: Exception) {
            return Resource.Failure(e.message ?: "error occured")
        }
    }

    suspend fun getRates(apiKey: String, base: String): Resource<CurrencyModel> {
        return try {
            val response = api.getRates(apiKey, base)
            val result = response.body()
            //saving in Room DB
            appDAO.insertRates(result)
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Failure(response.message())
            }
        } catch (e: Exception) {
            Resource.Failure(e.message ?: "error occured")
        }
    }

    fun checkTimeGap(savedTime: Long, newTime: Long): Boolean {
        var minutesPassed: Long = 0
        Log.e(TAG, "checkTimeGap: new: $newTime")
        Log.e(TAG, "checkTimeGap: saved: $savedTime")
        minutesPassed = ((newTime - savedTime) / 1000) / 60 //minutes
        Log.e(TAG, "checkTimeGap: $minutesPassed")
        if (minutesPassed > 4) return true
        else return false
//        return minutesPassed > 30
    }

    suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): Resource<String> {
        try {
            val response: Response<CurrencyModel>?
            val result: CurrencyModel?
            if (appDAO.getRates() != null) {
                Log.e(TAG, "convertCurrency: 1. room not null")
                //compare saved time in room with current time(if > 30mins)
                if (checkTimeGap(appDAO.getSavedTime(), System.currentTimeMillis())) {
                    // time > 30
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        response = api.getRates(Constants.apikey, "USD") // hit API
                        result = response.body()
                        if (response.isSuccessful && result != null) {
                            val time = System.currentTimeMillis()
                            val savedRates = saveAndGetRates(result, time)
                            val finalAmount = getFinalResult(
                                getRateForCurrency(fromCurrency, savedRates)!!,
                                getRateForCurrency(toCurrency, savedRates)!!, amount
                            )
                            return Resource.Success(finalAmount.toString())
                        } else {
                            return Resource.Failure(response.message())
                        }
                        /*--network available--*/
                        //hit API
                        //add/update to room
                        //add/update timestamp
                    } else {
                        val savedRates = appDAO.getRates().rates
                        val finalAmount = getFinalResult(
                            getRateForCurrency(fromCurrency, savedRates)!!,
                            getRateForCurrency(toCurrency, savedRates)!!, amount
                        )
                        return Resource.Success(finalAmount.toString())
                    }
                } else {
                    val savedRates = appDAO.getRates().rates
                    val finalAmount = getFinalResult(
                        getRateForCurrency(fromCurrency, savedRates)!!,
                        getRateForCurrency(toCurrency, savedRates)!!, amount
                    )
                    return Resource.Success(finalAmount.toString())
                    //time <= 30 mins
                    //display only
                    //no room saving
                    //no timeupdate
                }

            } else {
                //no data in room already
                if (NetworkUtils.isNetworkAvailable(context)) {
                    /*--network available--*/
                    //hit API
                    //add to room
                    //add timestamp
                    response = api.getRates(Constants.apikey, "USD") // hit API
                    result = response.body()
                    if (response.isSuccessful && result != null) {
                        val time = System.currentTimeMillis()
                        val savedRates = saveAndGetRates(result, time)
                        val finalAmount = getFinalResult(
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
            return Resource.Failure(e.message ?: "error occured")
        }
    }

    suspend fun saveAndGetRates(result: CurrencyModel, timeStamp: Long): Rates {
        appDAO.insertRates(result)
        appDAO.addTimeStamp(timeStamp)
        return appDAO.getRates().rates
    }

    fun getFinalResult(
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