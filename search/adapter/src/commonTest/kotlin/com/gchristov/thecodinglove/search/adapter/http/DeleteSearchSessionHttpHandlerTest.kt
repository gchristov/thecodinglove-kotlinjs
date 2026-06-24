package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchHttpRequest
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchRepository
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteSearchSessionHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Delete, config.method)
        assertEquals("/api/search/session", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun deleteErrorSendsError(): TestResult = runBlockingTest(
        deleteResult = Either.Left(Throwable("Session not found")),
    ) { handler, response ->
        handler.handleHttpRequest(FakeSearchHttpRequest(fakeSearchSessionId = TestSessionId), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Session not found"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun deleteSuccessSendsEmpty(): TestResult = runBlockingTest { handler, response ->
        handler.handleHttpRequest(FakeSearchHttpRequest(fakeSearchSessionId = TestSessionId), response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    @Test
    fun deletePassesSessionIdToRepository(): TestResult = runBlockingTest { handler, response ->
        val repository = FakeSearchRepository()
        val localHandler = DeleteSearchSessionHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            searchRepository = repository,
        )
        localHandler.handleHttpRequest(FakeSearchHttpRequest(fakeSearchSessionId = TestSessionId), response)
        repository.assertSessionDeleted(TestSessionId)
    }

    private fun runBlockingTest(
        deleteResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (DeleteSearchSessionHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = DeleteSearchSessionHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            searchRepository = FakeSearchRepository(deleteSearchSessionResult = deleteResult),
        )
        testBlock(handler, response)
    }
}

private const val TestSessionId = "session_123"
