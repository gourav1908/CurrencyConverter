package com.gourav.currencyconverter.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gourav.currencyconverter.data.models.CurrencyModel

@Dao
interface AppDAO {
    @Query("SELECT * from currency_list")
    fun getRates(): CurrencyModel

    @Insert
    suspend fun insertRates(currencyModel: CurrencyModel?)

    @Query("UPDATE currency_list SET dataTimeStamp = :dataTimeStamp")
    fun addTimeStamp(dataTimeStamp: Long?)

    @Query("SELECT dataTimeStamp from currency_list")
    fun getSavedTime(): Long

    @Query("DELETE from currency_list")
    fun deletePreviousData()
}