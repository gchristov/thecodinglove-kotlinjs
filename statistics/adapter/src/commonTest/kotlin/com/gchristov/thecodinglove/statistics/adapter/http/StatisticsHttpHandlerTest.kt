package com.gchristov.thecodinglove.statistics.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeEmptyHttpRequest
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.statistics.domain.model.StatisticsReport
import com.gchristov.thecodinglove.statistics.testfixtures.FakeStatisticsReportUseCase
import com.gchristov.thecodinglove.statistics.testfixtures.StatisticsReportCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class StatisticsHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/statistics", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun reportErrorSendsError(): TestResult = runBlockingTest(
        reportResult = Either.Left(Throwable("DB error")),
    ) { handler, response ->
        handler.handleHttpRequest(FakeEmptyHttpRequest, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"DB error"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun reportSuccessReturnsJson(): TestResult = runBlockingTest { handler, response ->
        handler.handleHttpRequest(FakeEmptyHttpRequest, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"search_statistics":{"messages_sent":100,"active_search_sessions":5,"messages_self_destruct":3},"slack_statistics":{"active_self_destruct_messages":2,"users":50,"teams":10}}""",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        reportResult: Either<Throwable, StatisticsReport> = Either.Right(StatisticsReportCreator.report()),
        testBlock: suspend (StatisticsHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = StatisticsHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            statisticsReportUseCase = FakeStatisticsReportUseCase(reportResult),
        )
        testBlock(handler, response)
    }
}
