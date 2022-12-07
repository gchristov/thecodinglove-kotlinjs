package com.gchristov.thecodinglove.kmpsearchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.kmpsearchdata.SearchException
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithHistoryUseCase
import kotlin.test.assertEquals

class FakeSearchWithHistoryUseCase(
    // Each result is passed as a response to the relevant invocation attempt
    var invocationResults: List<Either<SearchException, SearchWithHistoryUseCase.Result>>
) : SearchWithHistoryUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally

    private var invocations = 0

    override suspend fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
    ): Either<SearchException, SearchWithHistoryUseCase.Result> =
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