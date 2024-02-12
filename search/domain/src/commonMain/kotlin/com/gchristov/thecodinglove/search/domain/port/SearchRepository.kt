package com.gchristov.thecodinglove.search.domain.port

import arrow.core.Either
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Either<Throwable, Int>

    suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<SearchPost>>

    suspend fun getSearchSession(id: String): Either<Throwable, SearchSession>

    suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit>

    suspend fun deleteSearchSession(id: String): Either<Throwable, Unit>

    suspend fun findSearchSessions(state: SearchSession.State): Either<Throwable, List<SearchSession>>
}