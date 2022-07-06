package com.gourav.currencyconverter.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.repository.CurrencyRepository
import com.gourav.currencyconverter.utils.Constants
import com.gourav.currencyconverter.utils.DispatchersInterface
import com.gourav.currencyconverter.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONTokener
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CurrencyRepository,
    private val dispatcher: DispatchersInterface
) : ViewModel() {
    sealed class CurrencyEvents() {
        class Success(val resultMessage: String) : CurrencyEvents()
        class Error(val errorMessage: String) : CurrencyEvents()
        object Loading : CurrencyEvents()
        object Empty : CurrencyEvents()
    }

    private val TAG: String? = "viewm>>>>>"
    private val _conversion = MutableStateFlow<CurrencyEvents>(CurrencyEvents.Empty)
    val conversion: StateFlow<CurrencyEvents> = _conversion

    fun convert(amount: String, fromCurrency: String, toCurrency: String) {
        val fromAmount = amount.toFloatOrNull()
        if (fromAmount == null) {
            _conversion.value = CurrencyEvents.Error("Not a valid amount")
            return
        }

        viewModelScope.launch(dispatcher.io) {
            _conversion.value = CurrencyEvents.Loading
            /*val response = repository.getRates(Constants.apikey, fromCurrency)
//            Log.e(TAG, "convert: respo: ${response.data.toString()}")

            val moshi: Moshi = Moshi.Builder().build()
            val adapter: JsonAdapter<CurrencyModel> = moshi.adapter(CurrencyModel::class.java)
            val currency = adapter.fromJson(response.data.toString())

//            val currencyPojo = convertToObject(response.data.toString(), CurrencyPojo::class.java)
            Log.e(TAG, "convert: disclaimer: ${currency?.disclaimer}")*/


            when (val ratesResponse = repository.getData(Constants.apikey, fromCurrency)) {
                is Resource.Failure -> {
                    _conversion.value = CurrencyEvents.Error(ratesResponse.message!!)
                    Log.e(TAG, "convert: failure>>>: ${ratesResponse.message!!}")
                }
                is Resource.Success -> {
                    val rates = ratesResponse.data!!.rates
                    val rate = getRateForCurrency(toCurrency, rates)
                    Log.e(TAG, "convert: rate: $rate")
                    if (rate == null) {
                        _conversion.value = CurrencyEvents.Error("unexpected error!!")
                    } else {
                        val convertedCurrency = (fromAmount * rate * 100).roundToInt() / 100
                        _conversion.value = CurrencyEvents.Success(
                            "$fromAmount $fromCurrency = $convertedCurrency $toCurrency"
                        )
                    }
                }
            }
        }
    }

    //Use if response is in JSONObject as in {...}
    fun <T : Any> convertToObject(response: String, type: Class<T>): T {
        val json = JSONTokener(response).nextValue()
        return Gson().fromJson(json.toString(), type)
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

    /*private fun getRateModelForCurrency(currency: String, rates: Rates) = when (currency) {
        "AED" -> rates.aED
        "AFN" -> rates.aFN
        "ALL" -> rates.aLL
        "AMD" -> rates.aMD
        "ANG" -> rates.aNG
        "AOA" -> rates.aOA
        "ARS" -> rates.aRS
        "AUD" -> rates.aUD
        "AWG" -> rates.aWG
        "AZN" -> rates.aZN
        "BAM" -> rates.bAM
        "BBD" -> rates.bBD
        "BDT" -> rates.bDT
        "BGN" -> rates.bGN
        "BHD" -> rates.bHD
        "BIF" -> rates.bIF
        "BMD" -> rates.bMD
        "BND" -> rates.bND
        "BOB" -> rates.bOB
        "BRL" -> rates.bRL
        "BSD" -> rates.bSD
        "BTC" -> rates.bTC
        "BTN" -> rates.bTN
        "BWP" -> rates.bWP
        "BYN" -> rates.bYN
        "BZD" -> rates.bZD
        "CAD" -> rates.cAD
        "CDF" -> rates.cDF
        "CHF" -> rates.cHF
        "CLF" -> rates.cLF
        "CLP" -> rates.cLP
        "CNH" -> rates.cNH
        "CNY" -> rates.cNY
        "COP" -> rates.cOP
        "CRC" -> rates.cRC
        "CUC" -> rates.cUC
        "CUP" -> rates.cUP
        "CVE" -> rates.cVE
        "CZK" -> rates.cZK
        "DJF" -> rates.dJF
        "DKK" -> rates.dKK
        "DOP" -> rates.dOP
        "DZD" -> rates.dZD
        "EGP" -> rates.eGP
        "ERN" -> rates.eRN
        "ETB" -> rates.eTB
        "EUR" -> rates.eUR
        "FJD" -> rates.fJD
        "FKP" -> rates.fKP
        "GBP" -> rates.gBP
        "GEL" -> rates.eGP
        "GGP" -> rates.gGP
        "GHS" -> rates.gHS
        "GIP" -> rates.gIP
        "GMD" -> rates.gMD
        "GNF" -> rates.gNF
        "GTQ" -> rates.gTQ
        "GYD" -> rates.gYD
        "HKD" -> rates.hKD
        "HNL" -> rates.hNL
        "HRK" -> rates.hRK
        "HTG" -> rates.hTG
        "HUF" -> rates.hUF
        "IDR" -> rates.iDR
        "ILS" -> rates.iLS
        "IMP" -> rates.iMP
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
        "KMF" -> rates.kMF
        "KPW" -> rates.kPW
        "KRW" -> rates.kRW
        "KWD" -> rates.kWD
        "KYD" -> rates.kYD
        "KZT" -> rates.kZT
        "LAK" -> rates.lAK
        "LBP" -> rates.lBP
        "LKR" -> rates.lKR
        "LRD" -> rates.lRD
        "LSL" -> rates.lSL
        "LYD" -> rates.lYD
        "MAD" -> rates.mAD
        "MDL" -> rates.mDL
        "MGA" -> rates.mGA
        "MKD" -> rates.mKD
        "MMK" -> rates.mMK
        "MNT" -> rates.mNT
        "MOP" -> rates.mOP
        "MRU" -> rates.mRU
        "MUR" -> rates.mUR
        "MVR" -> rates.mVR
        "MWK" -> rates.mWK
        "MXN" -> rates.mXN
        "MYR" -> rates.mYR
        "MZN" -> rates.mZN
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
        "RSD" -> rates.rSD
        "RUB" -> rates.rUB
        "RWF" -> rates.rWF
        "SAR" -> rates.sAR
        "SBD" -> rates.sBD
        "SDG" -> rates.sDG
        "SEK" -> rates.sEK
        "SGD" -> rates.sGD
        "SHP" -> rates.sHP
        "SLL" -> rates.sLL
        "SOS" -> rates.sOS
        "SRD" -> rates.sRD
        "SSP" -> rates.sSP
        "STD" -> rates.sTD
        "STN" -> rates.sTN
        "SVC" -> rates.sVC
        "SYP" -> rates.sYP
        "SZL" -> rates.sZL
        "THB" -> rates.tHB
        "TJS" -> rates.tJS
        "TMT" -> rates.tMT
        "TND" -> rates.tND
        "TOP" -> rates.tOP
        "TRY" -> rates.tRY
        "TTD" -> rates.tTD
        "TWD" -> rates.tWD
        "TZS" -> rates.tZS
        "UAH" -> rates.uAH
        "UGX" -> rates.uGX
        "USD" -> rates.uSD
        "UYU" -> rates.uYU
        "UZS" -> rates.uZS
        "VES" -> rates.vES
        "VND" -> rates.vND
        "VUV" -> rates.vUV
        "WST" -> rates.wST
        "XAF" -> rates.xAF
        "XAG" -> rates.xAG
        "XAU" -> rates.xAU
        "XCD" -> rates.xCD
        "XDR" -> rates.xDR
        "XOF" -> rates.xOF
        "XPD" -> rates.xPD
        "XPF" -> rates.xPF
        "XPT" -> rates.xPT
        "YER" -> rates.yER
        "ZAR" -> rates.zAR
        "ZMW" -> rates.zMW
        "ZWL" -> rates.zWL
        else -> null

    }*/
}