package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSelfDestructPubSubHandlerTest {
    @Test
    fun handleSelfDestruct5MinInvokesSendUseCaseWith5Minutes(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        sendUseCase.assertInvokedOnce()
        sendUseCase.assertSelfDestructMinutes(5)
    }

    @Test
    fun handleOtherActionSkips(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.SHUFFLE).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        sendUseCase.assertNotInvoked()
    }

    @Test
    fun handleSelfDestructErrorReturnsLeft(): TestResult = runBlockingTest(
        sendResult = Either.Left(Throwable("Send failed"))
    ) { handler ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    private lateinit var sendUseCase: FakeSlackSendSearchUseCase

    private fun runBlockingTest(
        sendResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackSelfDestructPubSubHandler) -> Unit,
    ): TestResult = runTest {
        sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendResult)
        val handler = SlackSelfDestructPubSubHandler(
            slackSendSearchUseCase = sendUseCase,
            analytics = FakeAnalytics(),
        )
        testBlock(handler)
    }
}
