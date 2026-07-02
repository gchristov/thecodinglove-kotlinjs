package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.domain.usecase.SlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSendInteractivityPubSubHandlerTest {
    @Test
    fun handleSendActionInvokesSendUseCaseWithNoSelfDestruct(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        ensureAuthUseCase.assertInvokedOnce()
        sendUseCase.assertInvokedOnce()
        sendUseCase.assertSelfDestructMinutes(null)
    }

    @Test
    fun handleOtherActionSkips(): TestResult = runBlockingTest { handler ->
        val payload = interactivityMessage(action = SlackActionName.SHUFFLE).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        ensureAuthUseCase.assertNotInvoked()
        sendUseCase.assertNotInvoked()
    }

    @Test
    fun handleSendErrorReturnsLeft(): TestResult = runBlockingTest(
        sendResult = Either.Left(Throwable("Send failed"))
    ) { handler ->
        val payload = interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    @Test
    fun handleAuthenticationPromptSentSkipsSendUseCase(): TestResult = runBlockingTest(
        ensureAuthResult = Either.Right(SlackEnsureAuthenticatedUseCase.Result.AuthenticationPromptSent),
    ) { handler ->
        val payload = interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        sendUseCase.assertNotInvoked()
    }

    @Test
    fun handleEnsureAuthenticatedErrorReturnsLeft(): TestResult = runBlockingTest(
        ensureAuthResult = Either.Left(Throwable("Ensure auth failed")),
    ) { handler ->
        val payload = interactivityMessage(action = SlackActionName.SEND).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
        sendUseCase.assertNotInvoked()
    }

    private lateinit var ensureAuthUseCase: FakeSlackEnsureAuthenticatedUseCase
    private lateinit var sendUseCase: FakeSlackSendSearchUseCase

    private fun runBlockingTest(
        ensureAuthResult: Either<Throwable, SlackEnsureAuthenticatedUseCase.Result> =
            Either.Right(SlackEnsureAuthenticatedUseCase.Result.Authenticated),
        sendResult: Either<Throwable, SlackSelfDestructMessage?> = Either.Right(null),
        testBlock: suspend (SlackSendInteractivityPubSubHandler) -> Unit,
    ): TestResult = runTest {
        ensureAuthUseCase = FakeSlackEnsureAuthenticatedUseCase(invocationResult = ensureAuthResult)
        sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendResult)
        val handler = SlackSendInteractivityPubSubHandler(
            slackEnsureAuthenticatedUseCase = ensureAuthUseCase,
            slackSendSearchUseCase = sendUseCase,
            analytics = FakeAnalytics(),
        )
        testBlock(handler)
    }
}
