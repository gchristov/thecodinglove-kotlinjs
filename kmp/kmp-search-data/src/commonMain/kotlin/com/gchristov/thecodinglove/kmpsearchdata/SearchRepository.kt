package com.gchristov.thecodinglove.kmpsearchdata

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Int

    suspend fun search(
        page: Int,
        query: String
    ): List<Post>
}