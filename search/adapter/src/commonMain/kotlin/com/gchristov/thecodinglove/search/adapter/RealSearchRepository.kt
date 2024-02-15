package com.gchristov.thecodinglove.search.adapter

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.search.adapter.db.DbSearchSession
import com.gchristov.thecodinglove.search.adapter.db.mapper.toSearchSession
import com.gchristov.thecodinglove.search.adapter.htmlparser.mapper.toPost
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlPostsUseCase
import com.gchristov.thecodinglove.search.adapter.htmlparser.usecase.ParseHtmlTotalPostsUseCase
import com.gchristov.thecodinglove.search.adapter.http.TheCodingLoveApi
import com.gchristov.thecodinglove.search.domain.model.SearchPost
import com.gchristov.thecodinglove.search.domain.model.SearchSession
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import io.ktor.client.statement.*

internal class RealSearchRepository(
    private val theCodingLoveApi: TheCodingLoveApi,
    private val parseHtmlTotalPostsUseCase: ParseHtmlTotalPostsUseCase,
    private val parseHtmlPostsUseCase: ParseHtmlPostsUseCase,
    private val firebaseAdmin: FirebaseAdmin,
    private val jsonSerializer: JsonSerializer,
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Either<Throwable, Int> = try {
        val responseHtml = theCodingLoveApi.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        parseHtmlTotalPostsUseCase(ParseHtmlTotalPostsUseCase.Dto(responseHtml))
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error during finding total posts${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun search(
        page: Int,
        query: String
    ): Either<Throwable, List<SearchPost>> = try {
        val responseHtml = theCodingLoveApi.search(
            page = page,
            query = query
        ).bodyAsText()
        parseHtmlPostsUseCase(ParseHtmlPostsUseCase.Dto(responseHtml)).map { posts -> posts.map { it.toPost() } }
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
                    } ?: Either.Left(Throwable("Search session not found: searchSessionId=$id"))
                }
            } else {
                Either.Left(Throwable("Search session not found: searchSessionId=$id"))
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
        .mapLeft { Throwable("Search session not found: searchSessionId=${searchSession.id}") }

    override suspend fun deleteSearchSession(id: String): Either<Throwable, Unit> = firebaseAdmin
        .firestore()
        .collection(SearchSessionCollection)
        .document(id)
        .delete()

    override suspend fun findSearchSessions(
        state: SearchSession.State
    ): Either<Throwable, List<SearchSession>> = firebaseAdmin
        .firestore()
        .collection(SearchSessionCollection)
        .where("state.type", "==", state.type)
        .get()
        .flatMap {
            it.docs
                .map { document ->
                    document.decodeDataFromJson(
                        jsonSerializer = jsonSerializer,
                        strategy = DbSearchSession.serializer(),
                    ).flatMap { dbSearchSession ->
                        dbSearchSession?.let {
                            Either.Right(it.toSearchSession())
                        } ?: Either.Left(Throwable("Could not decode search session"))
                    }
                }
                .let { l -> either { l.bindAll() } }
        }
}

private const val SearchSessionCollection = "search_session"