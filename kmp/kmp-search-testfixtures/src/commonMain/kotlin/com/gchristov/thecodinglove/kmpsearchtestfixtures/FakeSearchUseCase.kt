package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchUseCase
import kotlin.test.assertEquals

class FakeSearchUseCase(
    // Each result is passed as a response to the relevant invocation attempt
    var invocationResults: List<SearchUseCase.Result>
) : SearchUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally

    private var invocations = 0

    override suspend fun invoke(
        query: String,
        totalPosts: Int?,
        searchHistory: Map<Int, List<Int>>,
        resultsPerPage: Int
    ): SearchUseCase.Result = searchResponse.execute(invocationResults[invocations++])

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