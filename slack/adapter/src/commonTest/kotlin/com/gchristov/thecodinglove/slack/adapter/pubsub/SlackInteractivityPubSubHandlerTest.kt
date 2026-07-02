package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.domain.model.SlackActionName
import com.gchristov.thecodinglove.slack.domain.model.SlackSentMessage
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackCancelSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackEnsureAuthenticatedUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackShuffleSearchUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import com.gchristov.thecodinglove.slack.testfixtures.SlackSentMessageCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackInteractivityPubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/slack/interactivity", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun sendActionRoutesToSendHandler(): TestResult = runBlockingTest { handler, sendUseCase, _ ->
        val result = handler.handle(interactivityMessage(action = SlackActionName.SEND))
        assertTrue { result.isRight() }
        sendUseCase.assertInvokedOnce()
    }

    @Test
    fun shuffleActionRoutesToShuffleHandler(): TestResult = runBlockingTest { handler, _, shuffleUseCase ->
        val result = handler.handle(interactivityMessage(action = SlackActionName.SHUFFLE))
        assertTrue { result.isRight() }
        shuffleUseCase.assertInvokedOnce()
    }

    @Test
    fun selfDestructActionRoutesToSelfDestructHandler(): TestResult = runBlockingTest { handler, sendUseCase, _ ->
        val result = handler.handle(interactivityMessage(action = SlackActionName.SELF_DESTRUCT_5_MIN))
        assertTrue { result.isRight() }
        sendUseCase.assertInvokedOnce()
    }

    @Test
    fun cancelActionRoutesToCancelHandler(): TestResult = runBlockingTest { handler, _, _ ->
        val result = handler.handle(interactivityMessage(action = SlackActionName.CANCEL))
        assertTrue { result.isRight() }
    }

    @Test
    fun handleErrorSwallows(): TestResult = runBlockingTest(
        sendResult = Either.Left(Throwable("send failed"))
    ) { handler, _, _ ->
        val result = handler.handle(interactivityMessage(action = SlackActionName.SEND))
        assertTrue { result.isRight() }
    }

    @Test
    fun handleParseErrorSendsEmpty(): TestResult = runBlockingTest { handler, _, _ ->
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
        sendResult: Either<Throwable, SlackSentMessage> = Either.Right(SlackSentMessageCreator.futureMessage()),
        testBlock: suspend (SlackInteractivityPubSubHandler, FakeSlackSendSearchUseCase, FakeSlackShuffleSearchUseCase) -> Unit,
    ): TestResult = runTest {
        val ensureAuthUseCase = FakeSlackEnsureAuthenticatedUseCase()
        val sendUseCase = FakeSlackSendSearchUseCase(invocationResult = sendResult)
        val shuffleUseCase = FakeSlackShuffleSearchUseCase()
        val cancelUseCase = FakeSlackCancelSearchUseCase()
        val analytics = FakeAnalytics()
        val handler = SlackInteractivityPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            eventHandlers = listOf(
                SlackSendInteractivityPubSubHandler(
                    slackEnsureAuthenticatedUseCase = ensureAuthUseCase,
                    slackSendSearchUseCase = sendUseCase,
                    analytics = analytics,
                ),
                SlackSelfDestructInteractivityPubSubHandler(
                    jsonSerializer = JsonSerializer.Default,
                    slackEnsureAuthenticatedUseCase = ensureAuthUseCase,
                    slackSendSearchUseCase = sendUseCase,
                    pubSubPublisher = FakePubSubPublisher(),
                    slackConfig = SlackConfigCreator.slackConfig(),
                    analytics = analytics,
                ),
                SlackShuffleInteractivityPubSubHandler(shuffleUseCase, analytics),
                SlackCancelSearchInteractivityPubSubHandler(cancelUseCase, analytics),
            ),
            pubSubDecoder = FakePubSubDecoder(FakePubSubRequest(null, SlackInteractivityReceivedEvent.serializer())),
        )
        testBlock(handler, sendUseCase, shuffleUseCase)
    }
}

internal typealias SlackInteractivityPayload = SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage

internal fun interactivityMessage(action: SlackActionName) = SlackInteractivityReceivedEvent(
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
