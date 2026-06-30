package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import com.gchristov.thecodinglove.search.testfixtures.FakePreloadSearchResultUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SearchPreloadPubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/search/preload", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleSuccessPreloads(): TestResult = runBlockingTest { handler, useCase ->
        val result = handler.handle(TestEvent)
        assertTrue { result.isRight() }
        useCase.assertInvokedOnce()
    }

    @Test
    fun handleErrorSwallows(): TestResult = runBlockingTest(
        preloadResult = Either.Left(Throwable("preload failed"))
    ) { handler, _ ->
        val result = handler.handle(TestEvent)
        assertTrue { result.isRight() }
    }

    @Test
    fun handleParseErrorSendsEmpty(): TestResult = runBlockingTest { handler, _ ->
        val response = FakeHttpResponse()
        val result = handler.handleError(Throwable("parse error"), response)
        assertTrue { result.isRight() }
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        preloadResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SearchPreloadPubSubHandler, FakePreloadSearchResultUseCase) -> Unit,
    ): TestResult = runTest {
        val useCase = FakePreloadSearchResultUseCase(invocationResult = preloadResult)
        val handler = SearchPreloadPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            pubSubDecoder = FakePubSubDecoder(FakePubSubRequest(null, SearchSessionResultCreatedEvent.serializer())),
            preloadSearchResultUseCase = useCase,
        )
        testBlock(handler, useCase)
    }
}

private val TestEvent = SearchSessionResultCreatedEvent(searchSessionId = "session_123")
