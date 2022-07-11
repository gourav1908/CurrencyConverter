package com.gourav.currencyconverter.data.room

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest : TestCase() {
    private lateinit var db: AppDatabase
    private lateinit var dao: AppDAO

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = db.getDao()
    }

    @After
    fun closeDB() {
        db.close()
    }

    @Test
    fun insertRates() = runBlocking {
        val rateList = mutableListOf<Rates>()

        /**
         * can also add the below dummy currencies in the rateList to test
         */
        /*rateList.add(Rates("USD", 1.0.toBigDecimal()))
        rateList.add(Rates("AUD", 1.458364.toBigDecimal()))
        rateList.add(Rates("CAD", 1.293664.toBigDecimal()))
        rateList.add(Rates("GBP", 0.83129.toBigDecimal()))
        rateList.add(Rates("JPY", 136.09.toBigDecimal()))
        rateList.add(Rates("INR", 79.3266.toBigDecimal()))*/

        val currencyModel =
            CurrencyModel(
                0,
                "USD",
                "Usage subject to terms: https://openexchangerates.org/terms",
                "https://openexchangerates.org/license",
                rateList,
                1657384578,
                1657384578
            )
        dao.insertResponse(currencyModel)

        val getRates = dao.getRates()
        assertThat(getRates.rates.containsAll(currencyModel.rates)).isTrue()
    }

    @Test
    fun deletePreviousData() = runBlocking {
        val currencyModel =
            CurrencyModel(
                0,
                "USD",
                "Usage subject to terms: https://openexchangerates.org/terms",
                "https://openexchangerates.org/license",
                ArrayList(),
                1657384578,
                1657384578
            )
        dao.insertResponse(currencyModel)
        dao.deletePreviousData()

        val getRates = dao.getRates()
        assertThat(getRates).isNull()
    }
}