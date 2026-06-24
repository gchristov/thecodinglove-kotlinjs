package com.gchristov.thecodinglove.selfdestruct.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.ParameterMap
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.SelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.DeserializationStrategy
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

private class FakeSelfDestructUseCase(
    private val result: Either<Throwable, Unit>,
) : SelfDestructUseCase {
    override suspend fun invoke() = result
}

private object FakeEmptyHttpRequest : HttpRequest {
    override val baseURL = ""
    override val hostname = ""
    override val ip = ""
    override val ips: Array<String>? = null
    override val method = ""
    override val path = ""
    override val protocol = ""
    override val headers: ParameterMap = object : ParameterMap { override fun <T> get(key: String): T? = null }
    override val query: ParameterMap = object : ParameterMap { override fun <T> get(key: String): T? = null }
    override val body: Any? = null
    override val bodyString: String? = null
    override fun <T> decodeBodyFromJson(jsonSerializer: JsonSerializer, strategy: DeserializationStrategy<T>): Either<Throwable, T?> = Either.Right(null)
}
