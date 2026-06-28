package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackCancelSearchUseCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackCancelInteractivityEventHandlerTest {
    @Test
    fun canHandleReturnsTrueForCancelAction(): TestResult = runBlockingTest { handler ->
        assertTrue { handler.canHandle(interactivityMessage(action = SlackActionName.CANCEL).payload as SlackInteractivityPayload) }
    }

    @Test
    fun canHandleReturnsFalseForOtherAction(): TestResult = runBlockingTest { handler ->
        assertFalse { handler.canHandle(interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload) }
    }

    @Test
    fun handleCancelInvokesCancelUseCase(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.CANCEL).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        cancelUseCase.assertInvokedOnce()
    }

    @Test
    fun handleCancelErrorReturnsLeft(): TestResult = runBlockingTest(
        cancelResult = Either.Left(Throwable("Cancel failed"))
    ) { handler ->
        val payload = interactivityMessage(action = SlackActionName.CANCEL).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    private lateinit var cancelUseCase: FakeSlackCancelSearchUseCase

    private fun runBlockingTest(
        cancelResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackCancelInteractivityEventHandler) -> Unit,
    ): TestResult = runTest {
        cancelUseCase = FakeSlackCancelSearchUseCase(invocationResult = cancelResult)
        val handler = SlackCancelInteractivityEventHandler(
            slackCancelSearchUseCase = cancelUseCase,
            analytics = FakeAnalytics(),
        )
        testBlock(handler)
    }
}
