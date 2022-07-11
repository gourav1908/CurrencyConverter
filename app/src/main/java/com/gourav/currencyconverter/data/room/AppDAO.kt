package com.gourav.currencyconverter.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gourav.currencyconverter.data.models.CurrencyModel

@Dao
interface AppDAO {
    @Query("SELECT * from currencies")
    fun getRates(): CurrencyModel

    @Insert
    suspend fun insertResponse(currencyModel: CurrencyModel)

    @Query("SELECT dataTimeStamp from currencies")
    fun getSavedTime(): Long

    @Query("DELETE from currencies")
    fun deletePreviousData()
}