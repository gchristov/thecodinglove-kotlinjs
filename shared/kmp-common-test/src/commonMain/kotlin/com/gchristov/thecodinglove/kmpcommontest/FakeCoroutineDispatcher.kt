package com.gchristov.thecodinglove.kmpcommontest

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Use this dispatcher to execute coroutines instantly and sequentially in unit tests.
 */
val FakeCoroutineDispatcher: CoroutineDispatcher = Dispatchers.Unconfined