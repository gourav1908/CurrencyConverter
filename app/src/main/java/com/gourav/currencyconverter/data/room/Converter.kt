package com.gourav.currencyconverter.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gourav.currencyconverter.data.models.Rates
import java.lang.reflect.Type

class Converter {

    @TypeConverter
    fun saveRateObject(rateList: List<Rates>?): String? {
        if (rateList == null) {
            return null
        }
        val gson = Gson()
        val type: Type =
            object : TypeToken<List<Rates>?>() {}.type
        return gson.toJson(rateList, type)
    }

    @TypeConverter
    fun retrieveRateObject(rateObjectStr: String?): List<Rates>? {
        if (rateObjectStr == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<List<Rates>?>() {}.type
        return gson.fromJson<List<Rates>?>(rateObjectStr, type)
    }
}