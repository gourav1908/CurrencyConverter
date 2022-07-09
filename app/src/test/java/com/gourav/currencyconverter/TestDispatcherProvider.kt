package com.gourav.currencyconverter

import com.gourav.currencyconverter.utils.DispatchersInterface
import kotlinx.coroutines.test.TestCoroutineDispatcher

class TestDispatcherProvider(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : DispatchersInterface {
    override val default
        get() = testDispatcher
    override val io
        get() = testDispatcher
    override val main
        get() = testDispatcher
    override val unconfined
        get() = testDispatcher
}