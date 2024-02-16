package com.gchristov.thecodinglove.search.proto.http

import arrow.core.Either
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchResult
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchSessionPost
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchStatistics
import com.gchristov.thecodinglove.search.proto.http.model.ApiUpdateSearchSessionState
import io.ktor.client.call.*

interface SearchServiceRepository {
    suspend fun search(query: String): Either<Throwable, ApiSearchResult>

    suspend fun shuffle(searchSessionId: String): Either<Throwable, ApiSearchResult>

    suspend fun deleteSearchSession(searchSessionId: String): Either<Throwable, Unit>

    suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: ApiUpdateSearchSessionState,
    ): Either<Throwable, Unit>

    suspend fun getSearchSessionPost(searchSessionId: String): Either<Throwable, ApiSearchSessionPost>

    suspend fun searchStatistics(): Either<Throwable, ApiSearchStatistics>
}

internal class RealSearchServiceRepository(private val searchServiceApi: SearchServiceApi) : SearchServiceRepository {
    override suspend fun search(query: String) = try {
        val response: ApiSearchResult = searchServiceApi.search(query).body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun shuffle(searchSessionId: String) = try {
        val response: ApiSearchResult = searchServiceApi.shuffle(searchSessionId).body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during shuffle${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun deleteSearchSession(searchSessionId: String) = try {
        searchServiceApi.deleteSearchSession(searchSessionId)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during delete search session${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun updateSearchSessionState(
        searchSessionId: String,
        state: ApiUpdateSearchSessionState,
    ) = try {
        searchServiceApi.updateSearchSessionState(state)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during update search session state${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun getSearchSessionPost(searchSessionId: String) = try {
        val response: ApiSearchSessionPost = searchServiceApi.getSearchSessionPost(searchSessionId).body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search session post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun searchStatistics() = try {
        val response: ApiSearchStatistics = searchServiceApi.searchStatistics().body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}