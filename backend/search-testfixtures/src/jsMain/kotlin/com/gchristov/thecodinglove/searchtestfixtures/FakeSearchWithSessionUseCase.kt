package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.searchdata.model.SearchError
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlin.test.assertEquals

class FakeSearchWithSessionUseCase(
    var invocationResult: Either<SearchError, SearchWithSessionUseCase.Result>
) : SearchWithSessionUseCase {
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally
    private var invocations = 0
    private var lastType: SearchWithSessionUseCase.Type? = null

    override suspend fun invoke(
        type: SearchWithSessionUseCase.Type
    ): Either<SearchError, SearchWithSessionUseCase.Result> {
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

    fun assertSearchType(type: SearchWithSessionUseCase.Type) {
        assertEquals(
            expected = type,
            actual = lastType
        )
    }
}