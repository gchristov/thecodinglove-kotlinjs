package com.gchristov.thecodinglove.slack.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.analyticstestfixtures.FakeAnalytics
import com.gchristov.thecodinglove.slack.adapter.pubsub.model.SlackSlashCommandReceivedEvent
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackMessageFactory
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackRepository
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackSearchRepository
import com.gchristov.thecodinglove.slack.testfixtures.SlackSearchResultCreator
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SlackSearchSlashCommandEventHandlerTest {
    @Test
    fun handleOtherCommandSkips(): TestResult = runBlockingTest { handler ->
        val result = handler.handle(TestSlashCommandEvent.copy(command = "/other"))
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(0)
    }

    @Test
    fun handleSearchSuccessPostsResultMessage(): TestResult = runBlockingTest(
        searchResult = Either.Right(SlackSearchResultCreator.success()),
    ) { handler ->
        val result = handler.handle(TestSlashCommandEvent)
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(2)
    }

    @Test
    fun handleSearchErrorPostsGenericError(): TestResult = runBlockingTest(
        searchResult = Either.Left(Throwable("Search error")),
    ) { handler ->
        val result = handler.handle(TestSlashCommandEvent)
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(2)
    }

    @Test
    fun handleSearchNoResultsPostsNoResultsMessage(): TestResult = runBlockingTest(
        searchResult = Either.Right(SlackSearchResultCreator.noResults()),
    ) { handler ->
        val result = handler.handle(TestSlashCommandEvent)
        assertTrue { result.isRight() }
        repository.assertPostMessageToUrlCalledTimes(2)
    }

    private lateinit var repository: FakeSlackRepository

    private fun runBlockingTest(
        searchResult: Either<Throwable, com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository.SearchResultDto> = Either.Right(SlackSearchResultCreator.success()),
        testBlock: suspend (SlackSearchSlashCommandEventHandler) -> Unit,
    ): TestResult = runTest {
        repository = FakeSlackRepository()
        val handler = SlackSearchSlashCommandEventHandler(
            slackRepository = repository,
            slackMessageFactory = FakeSlackMessageFactory(),
            slackSearchRepository = FakeSlackSearchRepository(searchResult = searchResult),
            analytics = FakeAnalytics(),
        )
        testBlock(handler)
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
