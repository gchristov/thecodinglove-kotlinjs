package com.gchristov.thecodinglove.searchdata

import arrow.core.Either
import arrow.core.flatMap
import com.gchristov.thecodinglove.commonfirebasedata.FirebaseAdmin
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.htmlparsedata.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.searchdata.db.DbSearchSession
import com.gchristov.thecodinglove.searchdata.db.toSearchSession
import com.gchristov.thecodinglove.searchdata.domain.*
import io.ktor.client.statement.*

interface SearchRepository {
    suspend fun getTotalPosts(query: String): Either<Throwable, Int>

    suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<Post>>

    suspend fun getSearchSession(id: String): Either<Throwable, SearchSession>

    suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit>

    suspend fun deleteSearchSession(id: String): Either<Throwable, Unit>
}

internal class RealSearchRepository(
    private val apiService: SearchApi,
    private val parseHtmlTotalPostsUseCase: ParseHtmlTotalPostsUseCase,
    private val parseHtmlPostsUseCase: ParseHtmlPostsUseCase,
    private val firebaseAdmin: FirebaseAdmin,
    private val jsonSerializer: JsonSerializer,
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Either<Throwable, Int> = try {
        val responseHtml = apiService.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        parseHtmlTotalPostsUseCase(responseHtml)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during finding total posts${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<Post>> = try {
        val responseHtml = apiService.search(
            page = page,
            query = query
        ).bodyAsText()
        parseHtmlPostsUseCase(responseHtml).map { posts -> posts.map { it.toPost() } }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during search${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun getSearchSession(id: String): Either<Throwable, SearchSession> = firebaseAdmin
        .firestore()
        .collection(SearchSessionCollection)
        .document(id)
        .get()
        .flatMap { document ->
            if (document.exists) {
                document.decodeDataFromJson(
                    jsonSerializer = jsonSerializer,
                    strategy = DbSearchSession.serializer(),
                ).flatMap { dbSearchSession ->
                    dbSearchSession?.let {
                        Either.Right(it.toSearchSession())
                    } ?: Either.Left(SearchError.SessionNotFound())
                }
            } else {
                Either.Left(SearchError.SessionNotFound())
            }
        }

    override suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(SearchSessionCollection)
        .document(searchSession.id)
        .set(
            jsonSerializer = jsonSerializer,
            strategy = DbSearchSession.serializer(),
            data = searchSession.toSearchSession(),
            merge = true,
        )

    override suspend fun deleteSearchSession(id: String): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(SearchSessionCollection)
        .document(id)
        .delete()
}

private const val SearchSessionCollection = "search_session"