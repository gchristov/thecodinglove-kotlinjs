package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession

class FakeSearchRepository(
    private val totalPosts: Int? = null,
    private val pages: Map<Int, List<Post>>? = null,
    private val searchSession: SearchSession? = null
) : SearchRepository {
    var totalPostsResponse: FakeResponse = FakeResponse.CompletesNormally
    var searchResponse: FakeResponse = FakeResponse.CompletesNormally
    var searchSessionResponse: FakeResponse = FakeResponse.CompletesNormally

    override suspend fun getTotalPosts(query: String): Int {
        return totalPostsResponse.execute(totalPosts!!)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): List<Post> {
        return searchResponse.execute(pages?.get(page) ?: emptyList())
    }

    override suspend fun getSearchSession(id: String): SearchSession {
        return searchSessionResponse.execute(searchSession!!)
    }
}