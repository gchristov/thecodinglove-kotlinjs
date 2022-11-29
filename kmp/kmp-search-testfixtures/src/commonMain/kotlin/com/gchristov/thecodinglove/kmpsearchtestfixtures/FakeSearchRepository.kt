package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FakeSearchRepository(
    private val totalPosts: Int? = null,
    private val pages: Map<Int, List<Post>>? = null,
    private val searchSession: SearchSession? = null
) : SearchRepository {
    var totalPostsResponse: FakeResponse = FakeResponse.CompletesNormally
    var searchResponse: FakeResponse = FakeResponse.CompletesNormally
    var searchSessionResponse: FakeResponse = FakeResponse.CompletesNormally

    private var searchSessionGetCalled = false
    private var lastSavedSession: SearchSession? = null

    override suspend fun getTotalPosts(query: String): Int {
        return totalPostsResponse.execute(totalPosts!!)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): List<Post> {
        return searchResponse.execute(pages?.get(page) ?: emptyList())
    }

    override suspend fun getSearchSession(id: String): SearchSession? {
        searchSessionGetCalled = true
        return searchSessionResponse.execute(searchSession)
    }

    override suspend fun saveSearchSession(searchSession: SearchSession) {
        lastSavedSession = searchSession
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