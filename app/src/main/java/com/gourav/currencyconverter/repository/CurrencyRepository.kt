package com.gourav.currencyconverter.repository

import android.content.Context
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.data.network.ApiInterface
import com.gourav.currencyconverter.data.room.AppDAO
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.utils.NetworkUtils
import com.gourav.currencyconverter.utils.ResponseState
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
                            return ResponseState.Success(finalAmount.toString())
                        } else {
                            return ResponseState.Failure(response.message())
                        }
                    } else {
                        val savedRates = appDAO.getRates().rates
                        val finalAmount = performConversion(
                            getRateForCurrency(fromCurrency, savedRates)!!,
                            getRateForCurrency(toCurrency, savedRates)!!, amount
                        )
                        return ResponseState.Success(finalAmount.toString())
                    }
                } else {
                    //time <= 30m, convert
                    val savedRates = appDAO.getRates().rates
                    val finalAmount = performConversion(
                        getRateForCurrency(fromCurrency, savedRates)!!,
                        getRateForCurrency(toCurrency, savedRates)!!, amount
                    )
                    return ResponseState.Success(finalAmount.toString())
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
                        return ResponseState.Success(finalAmount.toString())
                    } else {
                        return ResponseState.Failure(response.message())
                    }
                } else {
                    //no network
                    return ResponseState.Failure("Please connect to the internet!!")
                }
            }
        } catch (e: Exception) {
            //catch exception
            return ResponseState.Failure(e.message ?: "error occured")
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

    suspend fun saveAndGetRates(result: CurrencyModel, timeStamp: Long): Rates {
        appDAO.insertRates(result)
        appDAO.addTimeStamp(timeStamp)
        return appDAO.getRates().rates
    }

    private fun getRateForCurrency(currency: String, rates: Rates) = when (currency) {
        "AED" -> rates.aED
        "AFN" -> rates.aFN
        "ALL" -> rates.aLL
        "AMD" -> rates.aMD
        "AOA" -> rates.aOA
        "ARS" -> rates.aRS
        "AUD" -> rates.aUD
        "AZN" -> rates.aZN
        "BBD" -> rates.bBD
        "BDT" -> rates.bDT
        "BGN" -> rates.bGN
        "BHD" -> rates.bHD
        "BMD" -> rates.bMD
        "BOB" -> rates.bOB
        "BRL" -> rates.bRL
        "BSD" -> rates.bSD
        "BTC" -> rates.bTC
        "BTN" -> rates.bTN
        "BWP" -> rates.bWP
        "BYN" -> rates.bYN
        "CAD" -> rates.cAD
        "CDF" -> rates.cDF
        "CLF" -> rates.cLF
        "CLP" -> rates.cLP
        "CNH" -> rates.cNH
        "CNY" -> rates.cNY
        "COP" -> rates.cOP
        "CRC" -> rates.cRC
        "CUC" -> rates.cUC
        "CUP" -> rates.cUP
        "DKK" -> rates.dKK
        "DOP" -> rates.dOP
        "DZD" -> rates.dZD
        "EGP" -> rates.eGP
        "ETB" -> rates.eTB
        "EUR" -> rates.eUR
        "FJD" -> rates.fJD
        "GBP" -> rates.gBP
        "GEL" -> rates.gEL
        "GHS" -> rates.gHS
        "GNF" -> rates.gNF
        "GTQ" -> rates.gTQ
        "GYD" -> rates.gYD
        "HKD" -> rates.hKD
        "HRK" -> rates.hRK
        "HTG" -> rates.hTG
        "HUF" -> rates.hUF
        "IDR" -> rates.iDR
        "ILS" -> rates.iLS
        "INR" -> rates.iNR
        "IQD" -> rates.iQD
        "IRR" -> rates.iRR
        "ISK" -> rates.iSK
        "JEP" -> rates.jEP
        "JMD" -> rates.jMD
        "JOD" -> rates.jOD
        "JPY" -> rates.jPY
        "KES" -> rates.kES
        "KGS" -> rates.kGS
        "KHR" -> rates.kHR
        "KPW" -> rates.kPW
        "KRW" -> rates.kRW
        "KWD" -> rates.kWD
        "KZT" -> rates.kZT
        "LBP" -> rates.lBP
        "LKR" -> rates.lKR
        "LRD" -> rates.lRD
        "LYD" -> rates.lYD
        "MDL" -> rates.mDL
        "MGA" -> rates.mGA
        "MKD" -> rates.mKD
        "MMK" -> rates.mMK
        "MUR" -> rates.mUR
        "MVR" -> rates.mVR
        "MXN" -> rates.mXN
        "MYR" -> rates.mYR
        "NAD" -> rates.nAD
        "NGN" -> rates.nGN
        "NIO" -> rates.nIO
        "NOK" -> rates.nOK
        "NPR" -> rates.nPR
        "NZD" -> rates.nZD
        "OMR" -> rates.oMR
        "PAB" -> rates.pAB
        "PEN" -> rates.pEN
        "PGK" -> rates.pGK
        "PHP" -> rates.pHP
        "PKR" -> rates.pKR
        "PLN" -> rates.pLN
        "PYG" -> rates.pYG
        "QAR" -> rates.qAR
        "RON" -> rates.rON
        "RUB" -> rates.rUB
        "RWF" -> rates.rWF
        "SAR" -> rates.sAR
        "SDG" -> rates.sDG
        "SEK" -> rates.sEK
        "SGD" -> rates.sGD
        "SSP" -> rates.sSP
        "SVC" -> rates.sVC
        "SYP" -> rates.sYP
        "THB" -> rates.tHB
        "TJS" -> rates.tJS
        "TMT" -> rates.tMT
        "TND" -> rates.tND
        "TRY" -> rates.tRY
        "TWD" -> rates.tWD
        "TZS" -> rates.tZS
        "UAH" -> rates.uAH
        "UGX" -> rates.uGX
        "USD" -> rates.uSD
        "UYU" -> rates.uYU
        "UZS" -> rates.uZS
        "VES" -> rates.vES
        "VND" -> rates.vND
        "XAF" -> rates.xAF
        "XDR" -> rates.xDR
        "XPT" -> rates.xPT
        "YER" -> rates.yER
        "ZAR" -> rates.zAR
        "ZMW" -> rates.zMW
        "ZWL" -> rates.zWL
        else -> null
    }
}