package com.gourav.currencyconverter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gourav.currencyconverter.data.models.Rates
import com.gourav.currencyconverter.repository.RepositoryInterface
import com.gourav.currencyconverter.utils.DispatchersInterface
import com.gourav.currencyconverter.utils.ResponseState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: RepositoryInterface,
    private val dispatcher: DispatchersInterface
) : ViewModel() {
    sealed class CurrencyEvents {
        class Success(val resultList: List<Rates>) : CurrencyEvents()
        class Error(val errorMessage: String) : CurrencyEvents()
        object Loading : CurrencyEvents()
        object Empty : CurrencyEvents()
    }

    private val _conversion = MutableStateFlow<CurrencyEvents>(CurrencyEvents.Empty)
    val conversion: StateFlow<CurrencyEvents> = _conversion


    fun convertCurrency(amount: String, fromCurrency: String, toCurrency: String) {
        val fromAmount = amount.toBigDecimalOrNull()
        if (fromAmount == null || amount.toDouble() == 0.0) {
            _conversion.value = CurrencyEvents.Error("Not a valid amount")
            return
        }
        viewModelScope.launch(dispatcher.io) {
            _conversion.value = CurrencyEvents.Loading
            when (val result: ResponseState<String> =
                repository.getResult(fromCurrency, toCurrency, fromAmount)) {
                is ResponseState.Failure -> {
                    _conversion.value = CurrencyEvents.Error(result.message!!)
                }
                is ResponseState.Success -> {
                    _conversion.value =
                        CurrencyEvents.Success(result.convertedList)
                }
            }
        }
    }
}