package com.gchristov.thecodinglove.searchdata

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.htmlparse.HtmlPostParser
import com.gchristov.thecodinglove.searchdata.api.ApiSearchSession
import com.gchristov.thecodinglove.searchdata.model.*
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.statement.*

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Either<Throwable, Int>

    suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<Post>>

    suspend fun getSearchSession(id: String): Either<Throwable, SearchSession>

    suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit>
}

internal class RealSearchRepository(
    private val apiService: SearchApi,
    private val htmlPostParser: HtmlPostParser,
    private val firebaseFirestore: FirebaseFirestore,
    private val log: Logger,
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Either<Throwable, Int> = try {
        val response = apiService.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        htmlPostParser.parseTotalPosts(response)
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during finding total posts" }
        Either.Left(error)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<Post>> = try {
        val response = apiService.search(
            page = page,
            query = query
        ).bodyAsText()
        htmlPostParser.parsePosts(response).map { posts -> posts.map { it.toPost() } }
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during search" }
        Either.Left(error)
    }

    override suspend fun getSearchSession(id: String): Either<Throwable, SearchSession> = try {
        val document = firebaseFirestore
            .collection("searchSession")
            .document(id)
            .get()
        if (document.exists) {
            val apiSearchSession: ApiSearchSession = document.data()
            Either.Right(apiSearchSession.toSearchSession())
        } else {
            Either.Left(SearchError.SessionNotFound)
        }
    } catch (error: Throwable) {
        log.e(error) { error.message ?: "Error during finding search session" }
        Either.Left(error)
    }

    override suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit> =
        try {
            val document = firebaseFirestore
                .collection("searchSession")
                .document(searchSession.id)
            Either.Right(
                document.set(
                    data = searchSession.toSearchSession(),
                    encodeDefaults = true,
                    merge = true
                )
            )
        } catch (error: Throwable) {
            log.e(error) { error.message ?: "Error during saving search session" }
            Either.Left(error)
        }
}