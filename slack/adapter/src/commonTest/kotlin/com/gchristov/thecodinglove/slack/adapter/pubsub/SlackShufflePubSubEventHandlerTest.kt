package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackShuffleSearchUseCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackShufflePubSubEventHandlerTest {
    @Test
    fun handleShuffleInvokesShuffleUseCase(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.SHUFFLE).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        shuffleUseCase.assertInvokedOnce()
    }

    @Test
    fun handleOtherActionSkips(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        shuffleUseCase.assertNotInvoked()
    }

    @Test
    fun handleShuffleErrorReturnsLeft(): TestResult = runBlockingTest(
        shuffleResult = Either.Left(Throwable("Shuffle failed"))
    ) { handler ->
        val payload = interactivityMessage(action = SlackActionName.SHUFFLE).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    private lateinit var shuffleUseCase: FakeSlackShuffleSearchUseCase

    private fun runBlockingTest(
        shuffleResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackShufflePubSubEventHandler) -> Unit,
    ): TestResult = runTest {
        shuffleUseCase = FakeSlackShuffleSearchUseCase(invocationResult = shuffleResult)
        val handler = SlackShufflePubSubEventHandler(
            slackShuffleSearchUseCase = shuffleUseCase,
            analytics = FakeAnalytics(),
        )
        testBlock(handler)
    }
}
