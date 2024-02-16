package com.gchristov.thecodinglove.search.proto.http

import arrow.core.Either
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchResult
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchSessionPost
import com.gchristov.thecodinglove.search.proto.http.model.ApiSearchStatistics
import com.gchristov.thecodinglove.search.proto.http.model.ApiUpdateSearchSessionState
import io.ktor.client.call.*

interface SearchApiRepository {
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

internal class RealSearchApiRepository(private val searchApi: SearchApi) : SearchApiRepository {
    override suspend fun search(query: String) = try {
        val response: ApiSearchResult = searchApi.search(query).body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun shuffle(searchSessionId: String) = try {
        val response: ApiSearchResult = searchApi.shuffle(searchSessionId).body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during shuffle${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun deleteSearchSession(searchSessionId: String) = try {
        searchApi.deleteSearchSession(searchSessionId)
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
    ): Either<Throwable, Unit> = try {
        searchApi.updateSearchSessionState(state)
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during update search session state${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun getSearchSessionPost(searchSessionId: String) = try {
        val response: ApiSearchSessionPost = searchApi.getSearchSessionPost(searchSessionId).body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search session post${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun searchStatistics(): Either<Throwable, ApiSearchStatistics> = try {
        val response: ApiSearchStatistics = searchApi.searchStatistics().body()
        Either.Right(response)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search statistics${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}