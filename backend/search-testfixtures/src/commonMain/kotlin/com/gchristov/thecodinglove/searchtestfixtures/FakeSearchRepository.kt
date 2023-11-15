package com.gchristov.thecodinglove.searchtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commontest.FakeResponse
import com.gchristov.thecodinglove.commontest.execute
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.domain.Post
import com.gchristov.thecodinglove.searchdata.domain.SearchError
import com.gchristov.thecodinglove.searchdata.domain.SearchSession
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeSearchRepository(
    private val totalPosts: Int? = null,
    private val pages: Map<Int, List<Post>>? = null,
    private val searchSession: SearchSession? = null
) : SearchRepository {
    private val totalPostsResponse: FakeResponse = FakeResponse.CompletesNormally
    private val searchResponse: FakeResponse = FakeResponse.CompletesNormally
    private val searchSessionResponse: FakeResponse = FakeResponse.CompletesNormally

    private var searchSessionGetCalled = false
    private var lastSavedSession: SearchSession? = null

    override suspend fun getTotalPosts(query: String): Either<Throwable, Int> {
        return Either.Right(totalPostsResponse.execute(totalPosts!!))
    }

    override suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<Post>> {
        return Either.Right(searchResponse.execute(pages?.get(page) ?: emptyList()))
    }

    override suspend fun getSearchSession(id: String): Either<Throwable, SearchSession> {
        searchSessionGetCalled = true
        return searchSession?.let { Either.Right(searchSessionResponse.execute(it)) }
            ?: Either.Left(SearchError.SessionNotFound())
    }

    override suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit> {
        lastSavedSession = searchSession
        return Either.Right(Unit)
    }

    override suspend fun deleteSearchSession(id: String): Either<Throwable, Unit> {
        TODO("Not yet implemented")
    }

    fun assertSessionFetched() {
        assertTrue(searchSessionGetCalled)
    }

    fun assertSessionNotFetched() {
        assertFalse(searchSessionGetCalled)
    }

    fun assertSessionSaved(searchSession: SearchSession) {
        assertEquals(
            expected = searchSession,
            actual = lastSavedSession
        )
    }
}