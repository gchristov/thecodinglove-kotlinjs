package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.networktestfixtures.FakeHttpResponse
import com.gchristov.thecodinglove.common.pubsubtestfixtures.FakePubSubPublisher
import com.gchristov.thecodinglove.common.test.FakeCoroutineDispatcher
import com.gchristov.thecodinglove.common.test.FakeLogger
import com.gchristov.thecodinglove.search.adapter.pubsub.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchHttpRequest
import com.gchristov.thecodinglove.search.testfixtures.FakeSearchUseCase
import com.gchristov.thecodinglove.search.testfixtures.SearchResultCreator
import io.ktor.http.*
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SearchHttpHandlerTest {
    @Test
    fun httpConfig(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchUseCase.Error.Empty())
    ) { handler, _, _, _, _ ->
        val config = handler.httpConfig()
        assertEquals(HttpMethod.Get, config.method)
        assertEquals("/api/search", config.path)
        assertEquals(ContentType.Application.Json, config.contentType)
    }

    @Test
    fun handleRequestSuccessSearches(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = null,
            searchInvocationResult = Either.Right(expectedResult)
        ) { handler, _, searchUseCase, request, response ->
            handler.handleHttpRequest(
                request = request,
                response = response
            )
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchUseCase.Type.NewSession(query = TestSearchQuery)
            )
        }
    }

    @Test
    fun handleRequestSuccessPreloads(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = null,
            searchInvocationResult = Either.Right(expectedResult)
        ) { handler, pubSub, _, request, response ->
            handler.handleHttpRequest(
                request = request,
                response = response
            )
            pubSub.assertEquals(
                topic = TestPreloadSearchPubSubTopic,
                message = PreloadSearchPubSubMessage(TestSearchSessionId),
            )
        }
    }

    @Test
    fun handleRequestSuccessResponds(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = null,
            searchInvocationResult = Either.Right(expectedResult)
        ) { handler, _, _, request, response ->
            handler.handleHttpRequest(
                request = request,
                response = response
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
    fun handleRequestSuccessWithSessionIdSearches(): TestResult {
        val expectedResult = SearchResultCreator.validResult(
            searchSessionId = TestSearchSessionId,
            query = TestSearchQuery
        )
        return runBlockingTest(
            searchSessionId = TestSearchSessionId,
            searchQuery = TestSearchQuery,
            searchInvocationResult = Either.Right(expectedResult)
        ) { handler, _, searchUseCase, request, response ->
            handler.handleHttpRequest(
                request = request,
                response = response
            )
            searchUseCase.assertInvokedOnce()
            searchUseCase.assertSearchType(
                SearchUseCase.Type.WithSessionId(TestSearchSessionId)
            )
        }
    }

    @Test
    fun handleRequestErrorSearches(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchUseCase.Error.Empty(additionalInfo = "test"))
    ) { handler, _, searchUseCase, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        searchUseCase.assertInvokedOnce()
    }

    @Test
    fun handleRequestErrorDoesNotPreload(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchUseCase.Error.Empty(additionalInfo = "test"))
    ) { handler, pubSub, _, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        pubSub.assertNotInvoked()
    }

    @Test
    fun handleRequestEmptyErrorResponds(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchUseCase.Error.Empty(additionalInfo = "test"))
    ) { handler, _, _, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "{\"errorMessage\":\"No results found: test\"}",
            status = 400,
            filePath = null,
        )
    }

    @Test
    fun handleRequestSessionNotFoundErrorResponds(): TestResult = runBlockingTest(
        searchSessionId = TestSearchSessionId,
        searchQuery = TestSearchQuery,
        searchInvocationResult = Either.Left(SearchUseCase.Error.SessionNotFound(additionalInfo = "test"))
    ) { handler, _, _, request, response ->
        handler.handleHttpRequest(
            request = request,
            response = response
        )
        response.assertEquals(
            header = "Content-Type",
            headerValue = ContentType.Application.Json.toString(),
            data = "{\"errorMessage\":\"Session not found: test\"}",
            status = 400,
            filePath = null,
        )
    }

    private fun runBlockingTest(
        searchSessionId: String? = TestSearchSessionId,
        searchQuery: String? = TestSearchQuery,
        searchInvocationResult: Either<SearchUseCase.Error, SearchUseCase.Result>,
        testBlock: suspend (SearchHttpHandler, FakePubSubPublisher, FakeSearchUseCase, FakeSearchHttpRequest, FakeHttpResponse) -> Unit
    ): TestResult = runTest {
        val pubSubPublisher = FakePubSubPublisher()
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
            searchUseCase = searchUseCase,
            pubSubPublisher = pubSubPublisher,
            searchConfig = SearchConfig(
                postsPerPage = TestSearchPostsPerPage,
                preloadPubSubTopic = TestPreloadSearchPubSubTopic,
            )
        )
        testBlock(handler, pubSubPublisher, searchUseCase, request, response)
    }
}

private const val TestSearchQuery = "test"
private const val TestSearchSessionId = "session_123"
private const val TestSearchPostsPerPage = 4
private const val TestPreloadSearchPubSubTopic = "topic_123"