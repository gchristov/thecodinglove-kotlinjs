package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.adapter.pubsub.SearchSessionResultCreatedPubSubHandler
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import com.gchristov.thecodinglove.search.testfixtures.FakePreloadSearchResultUseCase
import com.gchristov.thecodinglove.search.testfixtures.SearchSessionResultCreatedPubSubCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchSessionResultCreatedPubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest(
        message = null,
        preloadSearchResultInvocationResult = Either.Left(Throwable()),
    ) { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/search/session-result-created", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleRequestSuccessPreloads(): TestResult = runBlockingTest(
        message = SearchSessionResultCreatedPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Right(Unit)
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    @Test
    fun handleRequestParseErrorDoesNotPreload(): TestResult = runBlockingTest(
        message = null,
        preloadSearchResultInvocationResult = Either.Left(Throwable())
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertNotInvoked()
        assertTrue { result.isRight() }
    }

    @Test
    fun handleRequestSearchErrorPreloads(): TestResult = runBlockingTest(
        message = SearchSessionResultCreatedPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Left(Throwable())
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    private fun runBlockingTest(
        message: SearchSessionResultCreatedEvent?,
        preloadSearchResultInvocationResult: Either<Throwable, Unit>,
        testBlock: suspend (SearchSessionResultCreatedPubSubHandler, FakePreloadSearchResultUseCase, FakePubSubRequest<SearchSessionResultCreatedEvent>) -> Unit
    ): TestResult = runTest {
        val preloadSearchResultUseCase = FakePreloadSearchResultUseCase(
            invocationResult = preloadSearchResultInvocationResult
        )
        val request = FakePubSubRequest(
            message = message,
            messageSerializer = SearchSessionResultCreatedEvent.serializer(),
        )
        val handler = SearchSessionResultCreatedPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            preloadSearchResultUseCase = preloadSearchResultUseCase,
            pubSubDecoder = FakePubSubDecoder(request),
        )
        testBlock(handler, preloadSearchResultUseCase, request)
    }
}
