package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmphtmlparse.HtmlPostParser
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.toPost
import io.ktor.client.statement.*

internal class RealSearchRepository(
    private val apiService: SearchApi,
    private val htmlPostParser: HtmlPostParser
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Int {
        val response = apiService.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        return htmlPostParser.parseTotalPosts(response)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): List<Post> {
        val response = apiService.search(
            page = page,
            query = query
        ).bodyAsText()
        return htmlPostParser.parsePosts(response).map { it.toPost() }
    }
}