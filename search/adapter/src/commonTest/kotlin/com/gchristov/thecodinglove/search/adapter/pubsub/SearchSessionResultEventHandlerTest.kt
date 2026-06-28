package com.gchristov.thecodinglove.search.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import com.gchristov.thecodinglove.search.testfixtures.FakePreloadSearchResultUseCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SearchSessionResultEventHandlerTest {
    @Test
    fun canHandleAlwaysReturnsTrue(): TestResult = runBlockingTest { handler ->
        assertTrue { handler.canHandle(TestEvent) }
    }

    @Test
    fun handleInvokesPreload(): TestResult = runBlockingTest(
        preloadResult = Either.Right(Unit)
    ) { handler ->
        val result = handler.handle(TestEvent)
        assertTrue { result.isRight() }
    }

    @Test
    fun handlePropagatesPreloadError(): TestResult = runBlockingTest(
        preloadResult = Either.Left(Throwable("preload failed"))
    ) { handler ->
        val result = handler.handle(TestEvent)
        assertFalse { result.isRight() }
    }

    private fun runBlockingTest(
        preloadResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SearchSessionResultEventHandler) -> Unit,
    ): TestResult = runTest {
        val handler = SearchSessionResultEventHandler(
            preloadSearchResultUseCase = FakePreloadSearchResultUseCase(invocationResult = preloadResult)
        )
        testBlock(handler)
    }
}

private val TestEvent = SearchSessionResultCreatedEvent(searchSessionId = "session_123")
