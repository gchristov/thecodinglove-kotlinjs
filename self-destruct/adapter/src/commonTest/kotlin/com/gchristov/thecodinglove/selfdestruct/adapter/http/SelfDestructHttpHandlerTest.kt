package com.gchristov.thecodinglove.selfdestruct.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeEmptyHttpRequest
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.selfdestruct.testfixtures.FakeSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SelfDestructHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest { handler, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/self-destruct", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun selfDestructErrorSendsError(): TestResult = runBlockingTest(
        selfDestructResult = Either.Left(Throwable("Self-destruct failed")),
    ) { handler, response ->
        handler.handleHttpRequest(FakeEmptyHttpRequest, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = """{"errorMessage":"Self-destruct failed"}""",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun selfDestructSuccessSendsEmpty(): TestResult = runBlockingTest { handler, response ->
        handler.handleHttpRequest(FakeEmptyHttpRequest, response)
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "",
            status = 200,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        selfDestructResult: Either<Throwable, Unit> = Either.Right(Unit),
        testBlock: suspend (SelfDestructHttpHandler, FakeHttpResponse) -> Unit,
    ): TestResult = runTest {
        val response = FakeHttpResponse()
        val handler = SelfDestructHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            selfDestructUseCase = FakeSelfDestructUseCase(selfDestructResult),
        )
        testBlock(handler, response)
    }
}
