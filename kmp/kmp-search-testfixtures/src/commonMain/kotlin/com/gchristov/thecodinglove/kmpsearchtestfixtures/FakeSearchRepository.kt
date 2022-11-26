package com.gchristov.thecodinglove.kmpsearchtestfixtures

import com.gchristov.thecodinglove.kmpcommontest.FakeResponse
import com.gchristov.thecodinglove.kmpcommontest.execute
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.SearchRepository

class FakeSearchRepository(
    private val totalPosts: Int? = null,
    private val pages: Map<Int, List<Post>>? = null,
) : SearchRepository {
    var totalPostsResponse: FakeResponse = FakeResponse.CompletesNormally
    var searchResponse: FakeResponse = FakeResponse.CompletesNormally

    override suspend fun getTotalPosts(query: String): Int {
        return totalPostsResponse.execute(totalPosts!!)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): List<Post> {
        return searchResponse.execute(pages?.get(page) ?: emptyList())
    }
}