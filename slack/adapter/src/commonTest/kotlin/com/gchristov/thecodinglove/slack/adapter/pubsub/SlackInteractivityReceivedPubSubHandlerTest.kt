package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackCancelSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackShuffleSearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackInteractivityReceivedPubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/slack/interactivity-received", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun nullBodySwallowsError(): TestResult = runBlockingTest(
        message = null,
    ) { handler, _, _, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
    }

    @Test
    fun sendActionInvokesSendUseCase(): TestResult = runBlockingTest(
        message = interactivityMessage(action = SlackActionName.SEND),
    ) { handler, sendUseCase, _, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
        sendUseCase.assertInvokedOnce()
        sendUseCase.assertSelfDestructMinutes(null)
    }

    @Test
    fun selfDestruct5MinActionInvokesSendUseCaseWith5Minutes(): TestResult = runBlockingTest(
        message = interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN),
    ) { handler, sendUseCase, _, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
        sendUseCase.assertInvokedOnce()
        sendUseCase.assertSelfDestructMinutes(5)
    }

    @Test
    fun shuffleActionInvokesShuffleUseCase(): TestResult = runBlockingTest(
        message = interactivityMessage(action = SlackActionName.SHUFFLE),
    ) { handler, _, shuffleUseCase, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
        shuffleUseCase.assertInvokedOnce()
    }

    @Test
    fun cancelActionInvokesCancelUseCase(): TestResult = runBlockingTest(
        message = interactivityMessage(action = SlackActionName.CANCEL),
    ) { handler, _, _, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
    }

    @Test
    fun sendActionErrorIsSwallowed(): TestResult = runBlockingTest(
        message = interactivityMessage(action = SlackActionName.SEND),
        sendResult = Either.Left(Throwable("Send failed")),
    ) { handler, _, _, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
    }

    private fun runBlockingTest(
        message: SlackInteractivityReceivedEvent? = interactivityMessage(action = SlackActionName.SEND),
        sendResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SlackInteractivityReceivedPubSubHandler, FakeSlackSendSearchUseCase, FakeSlackShuffleSearchUseCase, FakePubSubRequest<SlackInteractivityReceivedEvent>) -> Unit,
    ): TestResult = runTest {
        val sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendResult)
        val shuffleUseCase = FakeSlackShuffleSearchUseCase()
        val cancelUseCase = FakeSlackCancelSearchUseCase()
        val request = FakePubSubRequest(
            message = message,
            messageSerializer = SlackInteractivityReceivedEvent.serializer(),
        )
        val handler = SlackInteractivityReceivedPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            slackSendSearchUseCase = sendUseCase,
            slackShuffleSearchUseCase = shuffleUseCase,
            slackCancelSearchUseCase = cancelUseCase,
            analytics = FakeAnalytics(),
            pubSubDecoder = FakePubSubDecoder(request),
        )
        testBlock(handler, sendUseCase, shuffleUseCase, request)
    }
}

private fun interactivityMessage(action: SlackActionName) = SlackInteractivityReceivedEvent(
    payload = SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage(
        actions = listOf(
            SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage.Action(
                name = action.apiValue,
                value = "session_123",
            )
        ),
        team = SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage.Team(
            id = "team_id",
            domain = "team_domain",
        ),
        channel = SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage.Channel(
            id = "channel_id",
            name = "channel_name",
        ),
        user = SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage.User(
            id = "user_id",
            name = "user_name",
        ),
        responseUrl = "https://response.url",
    )
)
