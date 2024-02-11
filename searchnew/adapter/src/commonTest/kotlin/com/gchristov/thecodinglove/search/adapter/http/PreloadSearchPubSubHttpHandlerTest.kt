package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.adapter.pubsub.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.search.domain.model.SearchError
import com.gchristov.thecodinglove.search.testfixtures.FakePreloadSearchResultUseCase
import com.gchristov.thecodinglove.search.testfixtures.PreloadSearchPubSubCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreloadSearchPubSubHttpHandlerTest {
    @Test
    fun config(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty()),
    ) { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/search", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleRequestSuccessPreloads(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Right(Unit)
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    @Test
    fun handleRequestParseErrorDoesNotPreload(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty())
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertNotInvoked()
        assertTrue { result.isRight() }
    }

    @Test
    fun handleRequestSearchErrorPreloads(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty(additionalInfo = "test"))
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    private fun runBlockingTest(
        preloadSearchPubSubMessage: PreloadSearchPubSubMessage?,
        preloadSearchResultInvocationResult: Either<SearchError, Unit>,
        testBlock: suspend (PreloadSearchPubSubHandler, FakePreloadSearchResultUseCase, FakePubSubRequest<PreloadSearchPubSubMessage>) -> Unit
    ): TestResult = runTest {
        val preloadSearchResultUseCase = FakePreloadSearchResultUseCase(
            invocationResult = preloadSearchResultInvocationResult
        )
        val request = FakePubSubRequest(
            message = preloadSearchPubSubMessage,
            messageSerializer = PreloadSearchPubSubMessage.serializer(),
        )
        val handler = PreloadSearchPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            preloadSearchResultUseCase = preloadSearchResultUseCase,
            pubSubDecoder = FakePubSubDecoder(request),
        )
        testBlock(handler, preloadSearchResultUseCase, request)
    }
}
