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
import java.math.BigDecimal
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val api: ApiInterface, private val appDAO: AppDAO, private val context: Context
) : RepositoryInterface {
    override suspend fun getResult(
        fromCurrency: String,
        toCurrency: String,
        amount: BigDecimal
    ): ResponseState<String> {
        try {
            val response: Response<JsonElement>?
            val result: JsonElement?
            if (appDAO.getRates() != null) {
                /**
                 * compare saved time in room with current time(if > 30 minutes)
                 */
                if (checkTimeGap(appDAO.getSavedTime(), System.currentTimeMillis())) {
                    /** time > 30 */
                    if (NetworkUtils.isNetworkAvailable(context)) {
                        /**
                         * network available
                         * hit API, add to room, add timestamp, convert
                         */
                        response =
                            api.getRates(Constants.apikey, Constants.baseCurrency) // hit API
                        result = response.body()
                        return if (response.isSuccessful && result != null) {
                            val currencyModel = getCurrencyModel(
                                JSONObject(result.toString()),
                                System.currentTimeMillis()
                            )
                            val savedRates = saveAndGetRates(currencyModel)
                            performConversions(savedRates, fromCurrency, amount)
                            ResponseState.Success(
                                performConversions(
                                    savedRates,
                                    fromCurrency,
                                    amount
                                )
                            )
                        } else {
                            ResponseState.Failure(response.message())
                        }
                    } else {
                        val savedRates = appDAO.getRates().rates
                        performConversions(savedRates, fromCurrency, amount)
                        return ResponseState.Success(
                            performConversions(
                                savedRates,
                                fromCurrency,
                                amount
                            )
                        )
                    }
                } else {
                    /** time <= 30m, convert */
                    val savedRates = appDAO.getRates().rates
                    performConversions(savedRates, fromCurrency, amount)
                    return ResponseState.Success(
                        performConversions(
                            savedRates,
                            fromCurrency,
                            amount
                        )
                    )
                }

            } else {
                /** no data in room already */
                if (NetworkUtils.isNetworkAvailable(context)) {
                    /**
                     * network available
                     * hit API, add to room, add timestamp, convert
                     */
                    response = api.getRates(Constants.apikey, Constants.baseCurrency) // hit API
                    result = response.body()
                    return if (response.isSuccessful && result != null) {
                        val pojo = getCurrencyModel(
                            JSONObject(result.toString()),
                            System.currentTimeMillis()
                        )
                        val savedRates = saveAndGetRates(pojo)
                        performConversions(savedRates, fromCurrency, amount)
                        ResponseState.Success(performConversions(savedRates, fromCurrency, amount))
                    } else {
                        ResponseState.Failure(response.message())
                    }
                } else {
                    /** no network */
                    return ResponseState.Failure(context.getString(R.string.no_internet))
                }
            }
        } catch (e: Exception) {
            /** catch exception */
            return ResponseState.Failure(e.message ?: context.getString(R.string.error_occurred))
        }
    }

    private fun getCurrencyModel(jsonObject: JSONObject, timeStamp: Long): CurrencyModel {
        val ratesObj = jsonObject.getJSONObject(Constants.KEY_RATES)
        val currencyKeys = iterate(ratesObj.keys())
        val rates = ArrayList<Rates>()
        currencyKeys.map { rates.add(Rates(it, ratesObj.getDouble(it).toBigDecimal())) }

        return CurrencyModel(
            0,
            jsonObject.getString(Constants.KEY_BASE),
            jsonObject.getString(Constants.KEY_DISCLAIMER),
            jsonObject.getString(Constants.KEY_LICENSE),
            rates,
            jsonObject.getLong(Constants.KEY_TIMESTAMP),
            timeStamp
        )
    }

    private fun checkTimeGap(savedTime: Long, newTime: Long): Boolean {
        val minutesPassed: Long = ((newTime - savedTime) / 1000) / 60 //to minutes
        return minutesPassed > Constants.timeInterval
    }

    private fun performConversions(
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
        if (appDAO != null) {
            appDAO.deletePreviousData()
        }
        appDAO.insertResponse(result)
        return appDAO.getRates().rates
    }

    private fun <T> iterate(i: Iterator<T>): Iterable<T> {
        return object : Iterable<T> {
            override fun iterator(): Iterator<T> {
                return i
            }
        }
    }
}