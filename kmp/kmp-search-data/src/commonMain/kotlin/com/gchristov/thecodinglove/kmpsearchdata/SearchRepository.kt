package com.gchristov.thecodinglove.kmpsearchdata

import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Int

    suspend fun search(
        page: Int,
        query: String
    ): List<Post>

    suspend fun getSearchSession(id: String): SearchSession?

    suspend fun saveSearchSession(session: SearchSession)
}