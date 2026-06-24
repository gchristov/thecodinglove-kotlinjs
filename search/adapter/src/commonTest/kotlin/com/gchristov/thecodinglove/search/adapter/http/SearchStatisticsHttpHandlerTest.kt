package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchHttpRequest
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchStatisticsUseCase
import com.gchristov.thecodinglove.search.testfixtures.SearchStatisticsCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchStatisticsHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/search/statistics", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun statisticsErrorSendsError(): TestResult = runBlockingTest(
        statisticsResult = Either.Left(Throwable("DB error")),
    ) { handler, response ->
        handler.handleHttpRequest(FakeSearchHttpRequest(), response)
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
        handler.handleHttpRequest(FakeSearchHttpRequest(), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"messages_sent":10,"active_search_sessions":3,"messages_self_destruct":1}""",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        statisticsResult: Either<Throwable, com.gchristov.thecodinglove.search.domain.model.SearchStatistics> = Either.Right(SearchStatisticsCreator.statistics()),
        testBlock: suspend (SearchStatisticsHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = SearchStatisticsHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            statisticsUseCase = FakeSearchStatisticsUseCase(invocationResult = statisticsResult),
        )
        testBlock(handler, response)
    }
}
