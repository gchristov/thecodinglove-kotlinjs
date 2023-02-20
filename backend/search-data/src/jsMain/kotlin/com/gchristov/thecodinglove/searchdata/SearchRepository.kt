package com.gchristov.thecodinglove.searchdata

import arrow.core.Either
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
    private val firebaseFirestore: FirebaseFirestore
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Either<Throwable, Int> = try {
        val response = apiService.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        htmlPostParser.parseTotalPosts(response)
    } catch (error: Throwable) {
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
            Either.Left(error)
        }
}