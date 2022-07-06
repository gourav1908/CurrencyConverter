package com.gourav.currencyconverter.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gourav.currencyconverter.data.models.CurrencyModel

@Dao
interface AppDAO {
    @Query("select * from currency_list")
    fun getRates(): CurrencyModel

    @Insert
    suspend fun insertRates(currencyModel: CurrencyModel?)
}