package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.slack.domain.model.SlackStatistics
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackHttpRequest
import com.gchristov.thecodinglove.slack.testfixtures.FakeSlackStatisticsUseCase
import com.gchristov.thecodinglove.slack.testfixtures.SlackStatisticsCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SlackStatisticsHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/slack/statistics", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun statisticsErrorSendsError(): TestResult = runBlockingTest(
        statisticsResult = Either.Left(Throwable("DB error")),
    ) { handler, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"DB error"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun statisticsSuccessReturnsJson(): TestResult = runBlockingTest { handler, response ->
        handler.handleHttpRequest(FakeSlackHttpRequest(), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"active_self_destruct_messages":2,"users":10,"teams":5}""",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        statisticsResult: Either<Throwable, SlackStatistics> = Either.Right(SlackStatisticsCreator.statistics()),
        testBlock: suspend (SlackStatisticsHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = SlackStatisticsHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            statisticsUseCase = FakeSlackStatisticsUseCase(invocationResult = statisticsResult),
        )
        testBlock(handler, response)
    }
}
