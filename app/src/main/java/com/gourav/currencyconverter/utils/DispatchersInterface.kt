package com.gourav.currencyconverter.utils

import kotlinx.coroutines.CoroutineDispatcher

interface DispatchersInterface {
    val main: CoroutineDispatcher
    val io: CoroutineDispatcher
    val default: CoroutineDispatcher
    val unconfined: CoroutineDispatcher
}