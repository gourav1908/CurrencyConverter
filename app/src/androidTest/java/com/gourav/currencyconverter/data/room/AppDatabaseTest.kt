package com.gourav.currencyconverter.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import junit.framework.TestCase
import org.junit.After
import org.junit.Before
import org.junit.Test

class AppDatabaseTest : TestCase() {
    private lateinit var db: AppDatabase
    private lateinit var dao: AppDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).build()
        dao = db.getDao()
    }

    @After
    fun closeDB() {
        db.close()
    }

    @Test
    fun `insertRates`() {
        val rateList = mutableListOf<Rates>()
        rateList.add(Rates("USD", 1.0))
        rateList.add(Rates("AUD", 1.458364))
        rateList.add(Rates("CAD", 1.293664))
        rateList.add(Rates("GBP", 0.83129))
        rateList.add(Rates("JPY", 136.09))
        rateList.add(Rates("INR", 79.3266))
        val currencyModel =
            CurrencyModel(0, "Usd", "disclaimer", "license", rateList, 1657384578, 1657384578)
    }
}