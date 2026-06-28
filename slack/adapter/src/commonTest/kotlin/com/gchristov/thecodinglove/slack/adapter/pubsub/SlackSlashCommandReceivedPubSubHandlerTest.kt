package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubDecoder
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubRequest
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandReceivedEvent
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackSearchResultCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SlackSlashCommandReceivedPubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/slack/slash-command-received", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun nullBodyReturnsError(): TestResult = runBlockingTest(
        message = null,
    ) { handler, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isLeft() }
    }

    @Test
    fun commandRoutesToSearchHandler(): TestResult = runBlockingTest(
        searchResult = Either.Right(SlackSearchResultCreator.success()),
    ) { handler, request ->
        val result = handler.handlePubSubRequest(request)
        assertTrue { result.isRight() }
    }

    private fun runBlockingTest(
        message: SlackSlashCommandReceivedEvent? = TestSlashCommandEvent,
        searchResult: Either<Throwable, com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository.SearchResultDto> = Either.Right(SlackSearchResultCreator.success()),
        testBlock: suspend (SlackSlashCommandReceivedPubSubHandler, FakePubSubRequest<SlackSlashCommandReceivedEvent>) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository()
        val searchRepository = FakeSlackSearchRepository(searchResult = searchResult)
        val request = FakePubSubRequest(
            message = message,
            messageSerializer = SlackSlashCommandReceivedEvent.serializer(),
        )
        val handler = SlackSlashCommandReceivedPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            eventHandlers = listOf(
                SlackSearchSlashCommandEventHandler(
                    slackRepository = repository,
                    slackMessageFactory = FakeSlackMessageFactory(),
                    slackSearchRepository = searchRepository,
                    analytics = FakeAnalytics(),
                )
            ),
            pubSubDecoder = FakePubSubDecoder(request),
        )
        testBlock(handler, request)
    }
}

private val TestSlashCommandEvent = SlackSlashCommandReceivedEvent(
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
