package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmpsearchdata.model.Post

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Int

    suspend fun search(
        page: Int,
        query: String
    ): List<Post>
}