package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonservicetestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.commontest.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.commontest.FakeLogger
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchHttpRequest
import com.gchristov.thecodinglove.searchtestfixtures.FakeSearchUseCase
import com.gchristov.thecodinglove.searchtestfixtures.SearchResultCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchHttpHandlerTest {
    @Test
    fun config(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchError.Empty())
    ) { handler, _, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/search", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleRequestSuccess(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = null,
            searchInvocationResult = Either.Right(expectedResult)
        ) { handler, searchUseCase, request, response ->
            handler.handleHttpRequest(
                request = request,
                response = response
            )
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchUseCase.Type.NewSession(query = TestSearchQuery)
            )
            response.assertEquals(
                header = "Content-Type",
                headerValue = ContentType.Application.Json.toString(),
                data = """
                        {"search_session_id":"session_123","query":"test","post":{"title":"post","url":"url","image_url":"imageUrl"},"total_posts":1}
                       """.trimIndent(),
                status = 200,
                filePath = null,
            )
        }
    }

    @Test
    fun handleRequestSuccessWithSessionId(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = TestSearchSessionId,
            searchQuery = TestSearchQuery,
            searchInvocationResult = Either.Right(expectedResult)
        ) { handler, searchUseCase, request, response ->
            handler.handleHttpRequest(
                request = request,
                response = response
            )
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchUseCase.Type.WithSessionId(TestSearchSessionId)
            )
            response.assertEquals(
                header = "Content-Type",
                headerValue = ContentType.Application.Json.toString(),
                data = """
                        {"search_session_id":"session_123","query":"test","post":{"title":"post","url":"url","image_url":"imageUrl"},"total_posts":1}
                       """.trimIndent(),
                status = 200,
                filePath = null,
            )
        }
    }

    @Test
    fun handleError(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchError.Empty())
    ) { handler, searchUseCase, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        searchUseCase.assertInvokedOnce()
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "{\"errorMessage\":\"No results found\"}",
            status = 400,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        searchSessionId: String? = TestSearchSessionId,
        searchQuery: String? = TestSearchQuery,
        searchInvocationResult: Either<SearchError, SearchUseCase.Result>,
        testBlock: suspend (SearchHttpHandler, FakeSearchUseCase, FakeSearchHttpRequest, FakeHttpResponse) -> Unit
    ): TestResult = runTest {
        val searchUseCase = FakeSearchUseCase(
            invocationResult = searchInvocationResult
        )
        val request = FakeSearchHttpRequest(
            fakeSearchSessionId = searchSessionId,
            fakeSearchQuery = searchQuery
        )
        val response = FakeHttpResponse()
        val handler = SearchHttpHandler(
            dispatcher = FakeCoroutineDispatcher,
            jsonSerializer = JsonSerializer.Default,
            log = FakeLogger,
            searchUseCase = searchUseCase
        )
        testBlock(handler, searchUseCase, request, response)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"