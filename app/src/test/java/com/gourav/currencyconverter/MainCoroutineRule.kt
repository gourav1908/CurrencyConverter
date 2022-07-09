package com.gourav.currencyconverter

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
class MainCoroutineRule(
    val dispatcher: TestDispatcherProvider = TestDispatcherProvider()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(dispatcher.testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        dispatcher.testDispatcher.cleanupTestCoroutines()
        Dispatchers.resetMain()
    }
}