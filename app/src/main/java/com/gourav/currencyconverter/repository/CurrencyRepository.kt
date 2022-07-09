package com.gourav.currencyconverter.repository

import android.content.Context
import com.google.gson.JsonElement
import com.gourav.currencyconverter.R
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.data.network.ApiInterface
import com.gourav.currencyconverter.data.room.AppDAO
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.utils.NetworkUtils
import com.gourav.currencyconverter.utils.ResponseState
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val api: ApiInterface, private val appDAO: AppDAO, private val context: Context
) : RepositoryInterface {

    override suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: Double
    ): ResponseState<String> {
        try {
            val response: Response<JsonElement>?
            val result: JsonElement?
            if (appDAO.getRates() != null) {
                //compare saved time in room with current time(if > 30mins)
                if (checkTimeGap(appDAO.getSavedTime(), System.currentTimeMillis())) {
                    // time > 30
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        /*--network available--*/
                        //hit API, add to room, add timestamp, convert
                        response =
                            api.getRates2(Constants.apikey, Constants.baseCurrency) // hit API
                        result = response.body()
                        return if (response.isSuccessful && result != null) {
                            val time = System.currentTimeMillis()
                            val pojo = getCurrencyModel(JSONObject(result.toString()))
                            val savedRates = saveAndGetRates(pojo, time)
                            val finalAmount = performConversion(
                                getRateAmount(fromCurrency, savedRates),
                                getRateAmount(toCurrency, savedRates), amount
                            )
                            ResponseState.Success(finalAmount.toString())
                        } else {
                            ResponseState.Failure(response.message())
                        }
                    } else {
                        val savedRates = appDAO.getRates().rates
                        val finalAmount = performConversion(
                            getRateAmount(fromCurrency, savedRates),
                            getRateAmount(toCurrency, savedRates), amount
                        )
                        return ResponseState.Success(finalAmount.toString())
                    }
                } else {
                    //time <= 30m, convert
                    val savedRates = appDAO.getRates().rates
                    val finalAmount = performConversion(
                        getRateAmount(fromCurrency, savedRates),
                        getRateAmount(toCurrency, savedRates), amount
                    )
                    return ResponseState.Success(finalAmount.toString())
                }

            } else {
                //no data in room already
                if (NetworkUtils.isNetworkAvailable(context)) {
                    /*--network available--*/
                    //hit API, add to room, add timestamp, convert
                    response = api.getRates2(Constants.apikey, Constants.baseCurrency) // hit API
                    result = response.body()
                    return if (response.isSuccessful && result != null) {
                        val time = System.currentTimeMillis()
                        val pojo = getCurrencyModel(JSONObject(result.toString()))
                        val savedRates = saveAndGetRates(pojo, time)
                        val finalAmount = performConversion(
                            getRateAmount(fromCurrency, savedRates),
                            getRateAmount(toCurrency, savedRates), amount
                        )
                        ResponseState.Success(finalAmount.toString())
                    } else {
                        ResponseState.Failure(response.message())
                    }
                } else {
                    //no network
                    return ResponseState.Failure(context.getString(R.string.no_internet))
                }
            }
        } catch (e: Exception) {
            //catch exception
            return ResponseState.Failure(e.message ?: context.getString(R.string.error_occured))
        }
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

    suspend fun saveAndGetRates(result: CurrencyModel, timeStamp: Long): List<Rates> {
        appDAO.insertResponse(result)
        appDAO.addTimeStamp(timeStamp)
        return appDAO.getRates().rates
    }

    private fun getCurrencyModel(jsonObject: JSONObject): CurrencyModel {
        val ratesObj = jsonObject.getJSONObject(Constants.KEY_RATES)
        val currencyKeys = iterate(ratesObj.keys())
        val rates = ArrayList<Rates>()
        currencyKeys.map { rates.add(Rates(it, ratesObj.getDouble(it))) }

        return CurrencyModel(
            0,
            jsonObject.getString(Constants.KEY_BASE),
            jsonObject.getString(Constants.KEY_DISCLAIMER),
            jsonObject.getString(Constants.KEY_LICENSE),
            rates,
            jsonObject.getLong(Constants.KEY_TIMESTAMP),
            System.currentTimeMillis()
        )
    }

    private fun <T> iterate(i: Iterator<T>): Iterable<T> {
        return object : Iterable<T> {
            override fun iterator(): Iterator<T> {
                return i
            }
        }
    }

    private fun getRateAmount(currency: String, rateList: List<Rates>): Double {
        val value: Rates = rateList.single { it.currencyName == currency }
        return value.amount
    }
}