package com.gourav.currencyconverter.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.gourav.currencyconverter.data.room.Converter

@Entity(tableName = "currencies")
data class CurrencyModel(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    val base: String,
    val disclaimer: String,
    val license: String,
    @TypeConverters(Converter::class)
    @ColumnInfo(name = "rates")
    val rates: List<Rates>,
    val timestamp: Long,
    val dataTimeStamp: Long
)
