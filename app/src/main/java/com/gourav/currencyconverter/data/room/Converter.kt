package com.gourav.currencyconverter.data.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gourav.currencyconverter.data.models.Rates
import java.lang.reflect.Type

class Converter {

    @TypeConverter // note this annotation
    fun saveRateObject(rateObject: Rates?): String? {
        if (rateObject == null) {
            return null
        }
        val gson = Gson()
        val type: Type =
            object : TypeToken<Rates?>() {}.type
        return gson.toJson(rateObject, type)
    }

    @TypeConverter // note this annotation
    fun getImageListFromString(rateObjectStr: String?): Rates? {
        if (rateObjectStr == null) {
            return null
        }
        val gson = Gson()
        val type =
            object : TypeToken<Rates?>() {}.type
        return gson.fromJson<Rates>(rateObjectStr, type)
    }
}