package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import com.gchristov.thecodinglove.slack.domain.usecase.SlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSentMessageCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSelfDestructInteractivityPubSubHandlerTest {
    @Test
    fun handleSelfDestruct5MinInvokesSendUseCaseWith5Minutes(): TestResult = runBlockingTest { handler, ensureAuthUseCase, sendUseCase, _ ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        ensureAuthUseCase.assertInvokedOnce()
        sendUseCase.assertInvokedOnce()
        sendUseCase.assertSelfDestructMinutes(5)
    }

    @Test
    fun handleOtherActionSkips(): TestResult = runBlockingTest { handler, ensureAuthUseCase, sendUseCase, _ ->
        val payload = interactivityMessage(action = SlackActionName.SHUFFLE).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        ensureAuthUseCase.assertNotInvoked()
        sendUseCase.assertNotInvoked()
    }

    @Test
    fun handleSelfDestructErrorReturnsLeft(): TestResult = runBlockingTest(
        sendResult = Either.Left(Throwable("Send failed"))
    ) { handler, _, _, _ ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    @Test
    fun handleSelfDestructMessageSentSchedulesSelfDestruct(): TestResult = runBlockingTest(
        sendResult = Either.Right(SlackSentMessageCreator.pastMessage()),
    ) { handler, _, _, pubSubPublisher ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        pubSubPublisher.assertTopic(TestSelfDestructTopic)
    }

    @Test
    fun handleNonSelfDestructMessageReturnsLeft(): TestResult = runBlockingTest(
        sendResult = Either.Right(SlackSentMessageCreator.message()),
    ) { handler, _, _, pubSubPublisher ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
        pubSubPublisher.assertNotInvoked()
    }

    @Test
    fun handleAuthenticationPromptSentSkipsSendUseCaseAndDoesNotSchedule(): TestResult = runBlockingTest(
        ensureAuthResult = Either.Right(SlackEnsureAuthenticatedUseCase.Result.AuthenticationPromptSent),
    ) { handler, _, sendUseCase, pubSubPublisher ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        sendUseCase.assertNotInvoked()
        pubSubPublisher.assertNotInvoked()
    }

    @Test
    fun handleEnsureAuthenticatedErrorReturnsLeft(): TestResult = runBlockingTest(
        ensureAuthResult = Either.Left(Throwable("Ensure auth failed")),
    ) { handler, _, sendUseCase, pubSubPublisher ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
        sendUseCase.assertNotInvoked()
        pubSubPublisher.assertNotInvoked()
    }

    private fun runBlockingTest(
        ensureAuthResult: Either<Throwable, SlackEnsureAuthenticatedUseCase.Result> =
            Either.Right(SlackEnsureAuthenticatedUseCase.Result.Authenticated),
        sendResult: Either<Throwable, SlackSentMessage> = Either.Right(SlackSentMessageCreator.futureMessage()),
        testBlock: suspend (
            SlackSelfDestructInteractivityPubSubHandler,
            FakeSlackEnsureAuthenticatedUseCase,
            FakeSlackSendSearchUseCase,
            FakePubSubPublisher,
        ) -> Unit,
    ): TestResult = runTest {
        val ensureAuthUseCase = FakeSlackEnsureAuthenticatedUseCase(invocationResult = ensureAuthResult)
        val sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendResult)
        val pubSubPublisher = FakePubSubPublisher()
        val handler = SlackSelfDestructInteractivityPubSubHandler(
            jsonSerializer = JsonSerializer.Default,
            slackEnsureAuthenticatedUseCase = ensureAuthUseCase,
            slackSendSearchUseCase = sendUseCase,
            pubSubPublisher = pubSubPublisher,
            slackConfig = SlackConfigCreator.slackConfig(selfDestructMessagePubSubTopic = TestSelfDestructTopic),
            analytics = FakeAnalytics(),
        )
        testBlock(handler, ensureAuthUseCase, sendUseCase, pubSubPublisher)
    }
}

private const val TestSelfDestructTopic = "self_destruct_topic"
