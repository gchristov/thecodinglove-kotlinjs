package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlin.test.assertEquals

class FakeSearchUseCase(
    var invocationResult: Either<SearchError, SearchUseCase.Result>
) : SearchUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally
    private var invocations = 0
    private var lastType: SearchUseCase.Type? = null

    override suspend fun invoke(
        type: SearchUseCase.Type
    ): Either<SearchError, SearchUseCase.Result> {
        lastType = type
        invocations++
        return searchResponse.execute(invocationResult)
    }

    fun assertInvokedOnce() {
        assertEquals(
            expected = 1,
            actual = invocations
        )
    }

    fun assertSearchType(type: SearchUseCase.Type) {
        assertEquals(
            expected = type,
            actual = lastType
        )
    }
}