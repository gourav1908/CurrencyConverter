package com.gourav.currencyconverter.repository

import android.content.Context
import android.util.Log
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.network.ApiInterface
import com.gourav.currencyconverter.data.room.AppDAO
import com.gourav.currencyconverter.utils.NetworkUtils
import com.gourav.currencyconverter.utils.Resource
import retrofit2.Response
import javax.inject.Inject

class CurrencyRepository @Inject constructor(
    private val api: ApiInterface, private val appDAO: AppDAO, private val context: Context
) {

    private val TAG: String? = "repo>>>>>"

    /*suspend fun getRates(apiKey: String, base: String): Resource<JsonElement> {
        return try {
            val response = api.getRates(apiKey, base)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Failure(response.message())
            }
        } catch (e: Exception) {
            Resource.Failure(e.message ?: "error occured")
        }
    }*/

    suspend fun convertCurrency(apiKey: String, base: String): Resource<CurrencyModel?> {
        try {
            val response: Response<CurrencyModel>?
            val result: CurrencyModel?
            if (appDAO.getRates() != null) {
                //compare saved time in room with current time(if > 30mins)
                if (time > 30) {
                    return if (NetworkUtils.isNetworkAvailable(context)) {
                        /*--network available--*/
                        //hit API
                        //add to room
                        //add timestamp
                        response = api.getRates(apiKey, base) // hit API
                        result = response.body()
                        Resource.Success(result)
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
                    Resource.Success(result)
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
                //saving in Room DB
                appDAO.insertRates(result)
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

    fun checkTimeGap(savedTime: Int, newTime: Int): Int {
        var minutesPassed = 0
        minutesPassed = (newTime - savedTime) / 60
        return minutesPassed
    }
}