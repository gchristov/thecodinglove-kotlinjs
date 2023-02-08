package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.search.usecase.SearchWithHistoryUseCase
import com.gchristov.thecodinglove.searchdata.model.SearchError
import kotlin.test.assertEquals

class FakeSearchWithHistoryUseCase(
    // Each result is passed as a response to the relevant invocation attempt
    var invocationResults: List<Either<SearchError, SearchWithHistoryUseCase.Result>>
) : SearchWithHistoryUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally

    private var invocations = 0

    override suspend fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
    ): Either<SearchError, SearchWithHistoryUseCase.Result> =
        searchResponse.execute(invocationResults[invocations++])

    fun assertNotInvoked() {
        assertEquals(
            expected = 0,
            actual = invocations
        )
    }

    fun assertInvokedOnce() {
        assertEquals(
            expected = 1,
            actual = invocations
        )
    }

    fun assertInvokedTwice() {
        assertEquals(
            expected = 2,
            actual = invocations
        )
    }
}