package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.adapter.pubsub.PreloadSearchPubSubHandler
import com.gchristov.thecodinglove.search.adapter.pubsub.model.PubSubPreloadSearchMessage
import com.gchristov.thecodinglove.search.testfixtures.FakePreloadSearchResultUseCase
import com.gchristov.thecodinglove.search.testfixtures.PreloadSearchPubSubCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.DeserializationStrategy
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreloadSearchPubSubHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest(
        preloadSearchResultInvocationResult = Either.Left(Throwable()),
    ) { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/search", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleRequestSuccessPreloads(): TestResult = runBlockingTest(
        preloadSearchResultInvocationResult = Either.Right(Unit)
    ) { handler, preloadUseCase ->
        val result = handler.handlePubSubRequest(PreloadSearchPubSubCreator.defaultMessage())
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    @Test
    fun handleRequestSearchErrorPreloads(): TestResult = runBlockingTest(
        preloadSearchResultInvocationResult = Either.Left(Throwable())
    ) { handler, preloadUseCase ->
        val result = handler.handlePubSubRequest(PreloadSearchPubSubCreator.defaultMessage())
        preloadUseCase.assertInvokedOnce()
        preloadUseCase.assertSearchSessionId("session_123")
        assertTrue { result.isRight() }
    }

    private fun runBlockingTest(
        preloadSearchResultInvocationResult: Either<Throwable, Unit>,
        testBlock: suspend (PreloadSearchPubSubHandler, FakePreloadSearchResultUseCase) -> Unit
    ): TestResult = runTest {
        val preloadSearchResultUseCase = FakePreloadSearchResultUseCase(
            invocationResult = preloadSearchResultInvocationResult
        )
        val handler = PreloadSearchPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            preloadSearchResultUseCase = preloadSearchResultUseCase,
            pubSubDecoder = FakePubSubDecoder(object : PubSubRequest {
                override val bodyString: String?
                    get() = TODO("Not yet implemented")

                override fun <T> decodeBodyFromJson(
                    jsonSerializer: JsonSerializer,
                    strategy: DeserializationStrategy<T>
                ): Either<Throwable, T?> {
                    TODO("Not yet implemented")
                }
            }),
        )
        testBlock(handler, preloadSearchResultUseCase)
    }
}
