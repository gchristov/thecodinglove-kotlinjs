package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSubSubscription
import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpcommontest.FakeLogger
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubTopic
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchtestfixtures.FakePreloadSearchResultUseCase
import com.gchristov.thecodinglove.searchtestfixtures.PreloadSearchPubSubCreator
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PreloadSearchPubSubHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty),
    ) { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/search", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun pubSubConfig(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty),
    ) { handler, _, _ ->
        val config = handler.pubSubConfig()
        assertEquals(PreloadSearchPubSubTopic, config.topic)
    }

    @Test
    fun handleRequestSuccess(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Right(Unit)
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    @Test
    fun handleMessageError(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertEquals(
            expected = Either.Left(SearchError.Empty),
            actual = result,
        )
    }

    @Test
    fun handleMessageParseError(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { handler, preloadUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        preloadUseCase.assertNotInvoked()
        assertTrue { result.isLeft() }
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
            jsonSerializer = Json,
            log = FakeLogger,
            preloadSearchResultUseCase = preloadSearchResultUseCase,
            pubSubSubscription = FakePubSubSubscription(),
        )
        testBlock(handler, preloadSearchResultUseCase, request)
    }
}