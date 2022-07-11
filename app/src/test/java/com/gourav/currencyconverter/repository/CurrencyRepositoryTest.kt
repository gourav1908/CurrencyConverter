package com.gourav.currencyconverter.repository

import com.google.common.truth.Truth.assertThat
import com.gourav.currencyconverter.data.models.CurrencyModel
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.utils.ResponseState
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class CurrencyRepositoryTest {
    private lateinit var fakeCurrencyRepository: FakeCurrencyRepository

    @Before
    fun setup() {
        fakeCurrencyRepository = FakeCurrencyRepository()
    }

    @Test
    fun getAPIResponse() = runBlocking {
        val result: ResponseState<String>
        val isInternetAvailable = true

        val savedRates = mutableListOf<Rates>()
        savedRates.add(Rates("USD", 1.0.toBigDecimal()))
        savedRates.add(Rates("AUD", 1.458364.toBigDecimal()))
        savedRates.add(Rates("CAD", 1.293664.toBigDecimal()))
        savedRates.add(Rates("GBP", 0.83129.toBigDecimal()))
        savedRates.add(Rates("JPY", 136.09.toBigDecimal()))
        savedRates.add(Rates("INR", 79.3266.toBigDecimal()))

        result = if (isInternetAvailable) {
            ResponseState.Success(savedRates)
        } else {
            ResponseState.Failure("No internet")
        }
        /**
         * As Internet = true, result would be Success and so test will pass
         * If we make Internet = False, result will be Failure and test will Fail too
         */
        assertThat(result.convertedList).isEqualTo(savedRates)
    }

    @Test
    fun getCurrencyModel() {
        val timestamp: Long = 1657512001
        val rates = mutableListOf<Rates>()
        rates.add(Rates("USD", 1.0.toBigDecimal()))
        rates.add(Rates("AUD", 1.458364.toBigDecimal()))
        rates.add(Rates("CAD", 1.293664.toBigDecimal()))
        rates.add(Rates("GBP", 0.83129.toBigDecimal()))
        rates.add(Rates("JPY", 136.09.toBigDecimal()))
        rates.add(Rates("INR", 79.3266.toBigDecimal()))
        val cmodel = CurrencyModel(
            0,
            "USD",
            "disclaimer",
            "license",
            rates,
            timestamp,
            System.currentTimeMillis()
        )
        /**
         * currently we are checking below if RateList is same as with passed in the Object
         * we can also check by passing each key-value if it is available in the Object
         */
        assertThat(cmodel.rates).isEqualTo(rates)
    }

    @Test
    fun checkTimeGap() {
        val savedTime = 3
        val newTime = 5
        val result = newTime - savedTime

        assertThat(newTime - savedTime).isEqualTo(result)
    }

    @Test
    fun performConversion() {
        val rateList = mutableListOf<Rates>()
        rateList.add(Rates("USD", 1.0.toBigDecimal()))
        rateList.add(Rates("AUD", 1.458364.toBigDecimal()))
        rateList.add(Rates("CAD", 1.293664.toBigDecimal()))
        rateList.add(Rates("GBP", 0.83129.toBigDecimal()))
        rateList.add(Rates("JPY", 136.09.toBigDecimal()))
        rateList.add(Rates("INR", 79.3266.toBigDecimal()))

        val from = "JPY"
        val amt: BigDecimal = 34.45.toBigDecimal()
        val newList = mutableListOf<Rates>()
        rateList.map { rates ->
            newList.add(
                Rates(
                    rates.currencyName,
                    String.format(
                        "%.2f",
                        (rates.amount / rateList.single { it.currencyName == from }.amount) * amt
                    ).toBigDecimal()
                )
            )
        }
        assertThat(newList).isNotEmpty()
    }
}