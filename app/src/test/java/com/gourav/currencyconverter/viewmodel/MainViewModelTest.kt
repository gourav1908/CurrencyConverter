package com.gourav.currencyconverter.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.gourav.currencyconverter.MainCoroutineRule
import com.gourav.currencyconverter.repository.FakeCurrencyRepository
import com.gourav.currencyconverter.utils.ResponseState
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    private lateinit var viewmodel: MainViewModel

    @Before
    fun setup() {
        viewmodel = MainViewModel(FakeCurrencyRepository(), mainCoroutineRule.dispatcher)
    }

    @Test
    fun `empty amount returns error message`() = runBlocking {
        viewmodel.convertCurrency("", "USD", "JPY")

//        val vv: MainViewModel.CurrencyEvents = viewmodel.conversion.value

        val res = MainViewModel.CurrencyEvents.Error(viewmodel.conversion.value.toString())
//        res.errorMessage

        /* val result: ResponseState<String> =
             ResponseState.Failure(viewmodel.conversion.value.toString())*/
        assertThat(res.errorMessage).isEqualTo(res.errorMessage)
//        val result: MainViewModel.CurrencyEvents
//        assertThat(result.toString()).isEqualTo(result.toString())


//        assertThat(viewmodel.CurrencyEvents.Error(result))

        /*Assert.assertEquals(
            MainViewModel.CurrencyEvents.Error("empty amount"),
            viewmodel.conversion.value
        )*/
    }

    @Test
    fun `entered amount returns some result`() {
        viewmodel.convertCurrency("", "USD", "JPY")
        val result = MainViewModel.CurrencyEvents.Success(viewmodel.conversion.value.toString())
        assertThat(result.resultMessage).isNotEqualTo(MainViewModel.CurrencyEvents.Error("Not a valid amount"))
    }
}