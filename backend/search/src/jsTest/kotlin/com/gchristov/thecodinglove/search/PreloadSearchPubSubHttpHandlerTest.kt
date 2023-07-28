package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicetestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSub
import com.gchristov.thecodinglove.commonservicetestfixtures.FakePubSubHttpRequest
import com.gchristov.thecodinglove.kmpcommontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.kmpcommontest.FakeLogger
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.searchdata.model.PreloadSearchPubSubSubscription
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

@OptIn(ExperimentalCoroutinesApi::class)
class PreloadSearchPubSubHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty),
    ) { handler, _, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/pubsub/notifications", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun pubSubConfig(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty),
    ) { handler, _, _, _ ->
        val config = handler.pubSubConfig()
        assertEquals(PreloadSearchPubSubTopic, config.topic)
        assertEquals(PreloadSearchPubSubSubscription, config.subscription)
    }

    @Test
    fun handleRequestSuccess(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Right(Unit)
    ) { handler, preloadUseCase, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response,
        )
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    @Test
    fun handleMessageError(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = PreloadSearchPubSubCreator.defaultMessage(),
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { handler, preloadUseCase, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "{\"errorMessage\":\"No results found\"}",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun handleMessageParseError(): TestResult = runBlockingTest(
        preloadSearchPubSubMessage = null,
        preloadSearchResultInvocationResult = Either.Left(SearchError.Empty)
    ) { handler, preloadUseCase, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        preloadUseCase.assertNotInvoked()
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "{\"errorMessage\":\"PubSub body missing\"}",
            status = 400,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        preloadSearchPubSubMessage: PreloadSearchPubSubMessage?,
        preloadSearchResultInvocationResult: Either<SearchError, Unit>,
        testBlock: suspend (PreloadSearchPubSubHttpHandler, FakePreloadSearchResultUseCase, FakePubSubHttpRequest<PreloadSearchPubSubMessage>, FakeHttpResponse) -> Unit
    ): TestResult = runTest {
        val preloadSearchResultUseCase = FakePreloadSearchResultUseCase(
            invocationResult = preloadSearchResultInvocationResult
        )
        val request = FakePubSubHttpRequest(
            message = preloadSearchPubSubMessage,
            messageSerializer = PreloadSearchPubSubMessage.serializer(),
        )
        val response = FakeHttpResponse()
        val handler = PreloadSearchPubSubHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = Json,
            log = FakeLogger,
            preloadSearchResultUseCase = preloadSearchResultUseCase,
            pubSub = FakePubSub(),
        )
        testBlock(handler, preloadSearchResultUseCase, request, response)
    }
}