package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubMessage
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommontest.FakeLogger
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchtestfixtures.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PreloadSearchPubSubServiceTest {
    @Test
    fun register(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { service, _, _, register ->
        service.register()
        register.assertInvokedOnce()
    }

    @Test
    fun handleMessageSuccess(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Right(Unit)
    ) { service, preloadUseCase, message, register ->
        val actualResult = service.handleMessage(message)
        register.assertNotInvoked()
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertEquals(
            expected = Either.Right(Unit),
            actual = actualResult
        )
    }

    @Test
    fun handleMessageError(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { service, preloadUseCase, message, register ->
        val actualResult = service.handleMessage(message)
        register.assertNotInvoked()
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertEquals(
            expected = Either.Left(SearchError.Empty),
            actual = actualResult
        )
    }

    @Test
    fun handleMessageParseError(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { service, preloadUseCase, message, register ->
        val actualResult = service.handleMessage(message)
        register.assertNotInvoked()
        preloadUseCase.assertNotInvoked()
        assertTrue {
            actualResult.isLeft() &&
                    (actualResult as Either.Left).value.message == "Message body is null"
        }
    }

    private fun runBlockingTest(
        preloadSearchPubSubMessage: PreloadSearchPubSubMessage?,
        preloadSearchResultInvocationResult: Either<SearchError, Unit>,
        testBlock: suspend (PreloadSearchPubSubService, FakePreloadSearchResultUseCase, PubSubMessage, FakePubSubServiceRegister) -> Unit
    ): TestResult = runTest {
        val preloadSearchResultUseCase = FakePreloadSearchResultUseCase(
            invocationResult = preloadSearchResultInvocationResult
        )
        val message = FakePreloadSearchPubSubMessage(message = preloadSearchPubSubMessage)
        val register = FakePubSubServiceRegister()
        val service = PreloadSearchPubSubService(
            pubSubServiceRegister = register,
            jsonSerializer = Json,
            log = FakeLogger,
            preloadSearchResultUseCase = preloadSearchResultUseCase
        )
        testBlock(service, preloadSearchResultUseCase, message, register)
    }
}