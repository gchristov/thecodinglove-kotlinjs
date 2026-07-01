package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackSelfDestructMessage
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSelfDestructMessageCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSelfDestructInteractivityPubSubHandlerTest {
    @Test
    fun handleSelfDestruct5MinInvokesSendUseCaseWith5Minutes(): TestResult = runBlockingTest { handler, sendUseCase, _ ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        sendUseCase.assertInvokedOnce()
        sendUseCase.assertSelfDestructMinutes(5)
    }

    @Test
    fun handleOtherActionSkips(): TestResult = runBlockingTest { handler, sendUseCase, _ ->
        val payload = interactivityMessage(action = SlackActionName.SHUFFLE).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        sendUseCase.assertNotInvoked()
    }

    @Test
    fun handleSelfDestructErrorReturnsLeft(): TestResult = runBlockingTest(
        sendResult = Either.Left(Throwable("Send failed"))
    ) { handler, _, _ ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertFalse { result.isRight() }
    }

    @Test
    fun handleSelfDestructMessageSentSchedulesSelfDestruct(): TestResult = runBlockingTest(
        sendResult = Either.Right(SlackSelfDestructMessageCreator.pastMessage()),
    ) { handler, _, pubSubPublisher ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        pubSubPublisher.assertTopic(TestSelfDestructTopic)
    }

    @Test
    fun handleNoSelfDestructMessageDoesNotSchedule(): TestResult = runBlockingTest(
        sendResult = Either.Right(null),
    ) { handler, _, pubSubPublisher ->
        val payload = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN).payload as SlackInteractivityPayload
        val result = handler.handle(payload)
        assertTrue { result.isRight() }
        pubSubPublisher.assertNotInvoked()
    }

    private fun runBlockingTest(
        sendResult: Either<Throwable, SlackSelfDestructMessage?> = Either.Right(null),
        testBlock: suspend (SlackSelfDestructInteractivityPubSubHandler, FakeSlackSendSearchUseCase, FakePubSubPublisher) -> Unit,
    ): TestResult = runTest {
        val sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendResult)
        val pubSubPublisher = FakePubSubPublisher()
        val handler = SlackSelfDestructInteractivityPubSubHandler(
            jsonSerializer = JsonSerializer.Default,
            slackSendSearchUseCase = sendUseCase,
            pubSubPublisher = pubSubPublisher,
            slackConfig = SlackConfigCreator.slackConfig(selfDestructMessagePubSubTopic = TestSelfDestructTopic),
            analytics = FakeAnalytics(),
        )
        testBlock(handler, sendUseCase, pubSubPublisher)
    }
}

private const val TestSelfDestructTopic = "self_destruct_topic"
