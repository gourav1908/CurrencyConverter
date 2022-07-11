package com.gourav.currencyconverter.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class Constants {
    companion object {
        const val DB_NAME = "Currency_DB"
        const val BASE_URL = "https://openexchangerates.org/"
        const val apikey = "85d242322fe54db6bc15fbedb270a640"
        const val baseCurrency = "USD"
        const val timeInterval = 30 // to save bandwidth usage

        //API KEYS
        const val KEY_BASE = "base"
        const val KEY_DISCLAIMER = "disclaimer"
        const val KEY_LICENSE = "license"
        const val KEY_RATES = "rates"
        const val KEY_TIMESTAMP = "timestamp"

        fun hideKeyboard(context: Context, view: View) {
            val inputMethodManager =
                context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}