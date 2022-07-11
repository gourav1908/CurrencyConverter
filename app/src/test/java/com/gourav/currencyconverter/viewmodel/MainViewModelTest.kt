package com.gourav.currencyconverter.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.gourav.currencyconverter.MainCoroutineRule
import com.gourav.currencyconverter.repository.FakeCurrencyRepository
import com.gourav.currencyconverter.utils.ResponseState
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel(FakeCurrencyRepository(), mainCoroutineRule.dispatcher)
    }

    @Test
    fun `empty amount returns error message`() = runBlocking {
        val amount = ""
        val fromAMt = amount.toBigDecimalOrNull()

        viewModel.convertCurrency(fromAMt.toString(), "USD", "JPY")

        var re = MainViewModel.CurrencyEvents.Error("Not a valid amount")

        if (fromAMt == null) {
            val result: MainViewModel.CurrencyEvents = viewModel.conversion.value
            re = result as MainViewModel.CurrencyEvents.Error
        }
        assertThat(re.errorMessage).isEqualTo("Not a valid amount")
    }

    @Test
    fun `entered amount returns some result`() = runBlocking {
        val amount = "223.12"
        val fromAmt = amount.toBigDecimal()
        val result: ResponseState<String> =
            FakeCurrencyRepository().getResult("USD", "JPY", fromAmt)
        assertThat(result).isNotEqualTo("Not a valid amount")
    }
}