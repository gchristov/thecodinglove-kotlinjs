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

class SlackSearchPubSubHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Post, config.method)
        assertEquals("/api/pubsub/slack/search", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleSearchSuccessPostsResultMessage(): TestResult = runBlockingTest(
        searchResult = Either.Right(SlackSearchResultCreator.success()),
    ) { handler, repository, _ ->
        val result = handler.handle(TestSlashCommandEvent)
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(2)
    }

    @Test
    fun handleSearchErrorPostsGenericError(): TestResult = runBlockingTest(
        searchResult = Either.Left(Throwable("Search error")),
    ) { handler, repository, _ ->
        val result = handler.handle(TestSlashCommandEvent)
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(2)
    }

    @Test
    fun handleSearchNoResultsPostsNoResultsMessage(): TestResult = runBlockingTest(
        searchResult = Either.Right(SlackSearchResultCreator.noResults()),
    ) { handler, repository, _ ->
        val result = handler.handle(TestSlashCommandEvent)
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(2)
    }

    private fun runBlockingTest(
        searchResult: Either<Throwable, com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository.SearchResultDto> = Either.Right(SlackSearchResultCreator.success()),
        testBlock: suspend (SlackSearchPubSubHandler, FakeSlackRepository, FakeSlackSearchRepository) -> Unit,
    ): TestResult = runTest {
        val repository = FakeSlackRepository()
        val searchRepository = FakeSlackSearchRepository(searchResult = searchResult)
        val handler = SlackSearchPubSubHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            pubSubDecoder = FakePubSubDecoder(FakePubSubRequest(null, SlackSlashCommandReceivedEvent.serializer())),
            slackRepository = repository,
            slackMessageFactory = FakeSlackMessageFactory(),
            slackSearchRepository = searchRepository,
            analytics = FakeAnalytics(),
        )
        testBlock(handler, repository, searchRepository)
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
