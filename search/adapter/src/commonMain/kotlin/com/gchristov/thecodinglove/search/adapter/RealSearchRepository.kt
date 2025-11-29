package com.gchristov.thecodinglove.search.adapter

import arrow.core.Either
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
        Either.Left(
            Throwable(
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
        Either.Left(
            Throwable(
                message = "Error during search${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun getSearchSession(id: String): Either<Throwable, SearchSession> = either {
        val document = firebaseAdmin.firestore()
            .collection(SearchSessionCollection)
            .document(id)
            .get().bind()
        if (!document.exists) {
            raise(Throwable("Search session not found: searchSessionId=$id"))
        }

        val dbSearchSession = document.decodeDataFromJson(
            jsonSerializer = jsonSerializer,
            strategy = DbSearchSession.serializer(),
        ).bind()
        if (dbSearchSession == null) {
            raise(Throwable("Could not parse session: searchSessionId=$id"))
        }

        dbSearchSession.toSearchSession()
    }

    override suspend fun saveSearchSession(searchSession: SearchSession): Either<Throwable, Unit> =
        firebaseAdmin.firestore()
            .collection(SearchSessionCollection)
            .document(searchSession.id)
            .set(
                jsonSerializer = jsonSerializer,
                strategy = DbSearchSession.serializer(),
                data = searchSession.toSearchSession(),
                merge = true,
            )
            .mapLeft { Throwable("Could not save search session: searchSessionId=${searchSession.id}") }

    override suspend fun deleteSearchSession(id: String): Either<Throwable, Unit> = firebaseAdmin.firestore()
        .collection(SearchSessionCollection)
        .document(id)
        .delete()

    override suspend fun findSearchSessions(
        state: SearchSession.State
    ): Either<Throwable, List<SearchSession>> = either {
        val searchRes = firebaseAdmin.firestore()
            .collection(SearchSessionCollection)
            .where("state.type", "==", state.type)
            .get().bind()

        searchRes.docs.map { document ->
            val dbSearchSession = document.decodeDataFromJson(
                jsonSerializer = jsonSerializer,
                strategy = DbSearchSession.serializer(),
            ).bind()
            if (dbSearchSession == null) {
                raise(Throwable("Could not decode search session result"))
            }

            dbSearchSession.toSearchSession()
        }
    }
}

private const val SearchSessionCollection = "search_session"