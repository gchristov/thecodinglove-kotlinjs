package com.gchristov.thecodinglove.kmpsearchdata

import arrow.core.Either
import com.gchristov.thecodinglove.kmpsearchdata.model.Post
import com.gchristov.thecodinglove.kmpsearchdata.model.SearchSession

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Either<Exception, Int>

    suspend fun search(
        page: Int,
        query: String
    ): Either<Exception, List<Post>>

    suspend fun getSearchSession(id: String): SearchSession?

    suspend fun saveSearchSession(searchSession: SearchSession)
}