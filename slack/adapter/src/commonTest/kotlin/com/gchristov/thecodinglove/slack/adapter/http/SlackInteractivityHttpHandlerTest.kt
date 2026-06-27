package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackInteractivity
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackInteractivityReceivedEvent
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackVerifyRequestUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackInteractivityHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/slack/interactivity", config.path)
        assertEquals(ContentType.Application.FormUrlEncoded, config.contentType)
    }

    @Test
    fun nullBodyReturnsError(): TestResult = runBlockingTest(
        body = null,
    ) { handler, _, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(fakeBody = null), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Request body is invalid"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun validBodyPublishesToPubSub(): TestResult = runBlockingTest { handler, pubSub, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(fakeBody = TestInteractivity), response)
        pubSub.assertEquals(
            topic = TestSlackConfig.interactivityPubSubTopic,
            message = TestExpectedPubSubMessage,
        )
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    @Test
    fun pubSubErrorSendsError(): TestResult = runBlockingTest(
        publishResult = Either.Left(Throwable("PubSub error")),
    ) { handler, _, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(fakeBody = TestInteractivity), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"PubSub error"}""",
            status = 400,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        body: ApiSlackInteractivity? = TestInteractivity,
        publishResult: Either<Throwable, String> = Either.Right("msg_id"),
        testBlock: suspend (SlackInteractivityHttpHandler, FakePubSubPublisher, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val pubSub = FakePubSubPublisher(publishResult = publishResult)
        val response = FakeHttpResponse()
        val handler = SlackInteractivityHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            slackVerifyRequestUseCase = FakeSlackVerifyRequestUseCase(),
            slackConfig = TestSlackConfig,
            pubSubPublisher = pubSub,
        )
        testBlock(handler, pubSub, response)
    }
}

private val TestInteractivity = ApiSlackInteractivity(
    payload = ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage(
        actions = listOf(ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiAction(
            name = "send",
            value = "session_123",
        )),
        team = ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiTeam(
            id = "team_id",
            domain = "team_domain",
        ),
        channel = ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiChannel(
            id = "channel_id",
            name = "channel_name",
        ),
        user = ApiSlackInteractivity.ApiSlackInteractivityPayload.ApiInteractiveMessage.ApiUser(
            id = "user_id",
            name = "user_name",
        ),
        responseUrl = "https://response.url",
    )
)

private val TestExpectedPubSubMessage = SlackInteractivityReceivedEvent(
    payload = SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage(
        actions = listOf(SlackInteractivityReceivedEvent.InteractivityPayload.InteractiveMessage.Action(
            name = "send",
            value = "session_123",
        )),
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

private val TestSlackConfig = SlackConfigCreator.slackConfig()
