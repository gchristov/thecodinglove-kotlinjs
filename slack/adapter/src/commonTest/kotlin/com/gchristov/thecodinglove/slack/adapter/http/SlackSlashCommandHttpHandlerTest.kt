package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.http.model.ApiSlackSlashCommand
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandReceivedEvent
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackVerifyRequestUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackConfigCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackSlashCommandHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/slack/slash", config.path)
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
    fun validBodyPublishesSlashCommandReceivedEvent(): TestResult = runBlockingTest { handler, pubSub, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(fakeBody = TestSlashCommand), response)
        pubSub.assertEquals(
            topic = TestSlackConfig.slashCommandReceivedPubSubTopic,
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
    fun publishSlashCommandReceivedEventErrorSendsError(): TestResult = runBlockingTest(
        publishResult = Either.Left(Throwable("PubSub error")),
    ) { handler, _, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(fakeBody = TestSlashCommand), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"PubSub error"}""",
            status = 400,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        body: ApiSlackSlashCommand? = TestSlashCommand,
        publishResult: Either<Throwable, String> = Either.Right("msg_id"),
        testBlock: suspend (SlackSlashCommandHttpHandler, FakePubSubPublisher, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val pubSub = FakePubSubPublisher(publishResult = publishResult)
        val response = FakeHttpResponse()
        val handler = SlackSlashCommandHttpHandler(
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

private val TestSlashCommand = ApiSlackSlashCommand(
    teamId = "team_id",
    teamDomain = "team_domain",
    channelId = "channel_id",
    channelName = "channel_name",
    userId = "user_id",
    userName = "user_name",
    command = "/codinglove",
    text = "kotlin",
    responseUrl = "https://response.url",
)

private val TestExpectedPubSubMessage = SlackSlashCommandReceivedEvent(
    teamId = "team_id",
    teamDomain = "team_domain",
    channelId = "channel_id",
    channelName = "channel_name",
    userId = "user_id",
    userName = "user_name",
    command = "/codinglove",
    text = "kotlin",
    responseUrl = "https://response.url",
)

private val TestSlackConfig = SlackConfigCreator.slackConfig()
