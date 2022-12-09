package com.gchristov.thecodinglove.searchdata

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctions
import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctionsResponse
import com.gchristov.thecodinglove.commonfirebase.get
import com.gchristov.thecodinglove.htmlparse.HtmlPostParser
import com.gchristov.thecodinglove.searchdata.api.ApiSearchSession
import com.gchristov.thecodinglove.searchdata.model.*
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import dev.gitlive.firebase.firestore.FirebaseFirestore
import io.ktor.client.statement.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.Exception
import kotlin.Int
import kotlin.String
import kotlin.Unit
import kotlin.let

internal class RealSearchRepository(
    private val apiService: SearchApi,
    private val htmlPostParser: HtmlPostParser,
    private val firebaseFirestore: FirebaseFirestore
) : SearchRepository {
    override suspend fun getTotalPosts(query: String): Either<Exception, Int> {
        val response = apiService.search(
            // First page should always exist if there are results
            page = 1,
            query = query
        ).bodyAsText()
        return htmlPostParser.parseTotalPosts(response)
    }

    override suspend fun search(
        page: Int,
        query: String
    ): Either<Exception, List<Post>> {
        val response = apiService.search(
            page = page,
            query = query
        ).bodyAsText()
        return htmlPostParser.parsePosts(response).map { posts -> posts.map { it.toPost() } }
    }

    override suspend fun getSearchSession(id: String): SearchSession? {
        val document = firebaseFirestore
            .collection("searchSession")
            .document(id)
            .get()
        return if (document.exists) {
            val apiSearchSession: ApiSearchSession = document.data()
            apiSearchSession.toSearchSession()
        } else {
            null
        }
    }

    override suspend fun saveSearchSession(searchSession: SearchSession) {
        val document = firebaseFirestore
            .collection("searchSession")
            .document(searchSession.id)
        document.set(
            data = searchSession.toApiSearchSession(),
            encodeDefaults = true,
            merge = true
        )
    }

    override fun observeSearchRequest(
        callback: (
            request: Either<Exception, SearchWithSessionUseCase.Type>,
            response: FirebaseFunctionsResponse
        ) -> Unit
    ) = FirebaseFunctions.https.onRequest { request, response ->
        try {
            val searchQuery: String = request.query["searchQuery"] ?: "release"
            val searchSessionId: String? = request.query["searchSessionId"]
            val searchType = searchSessionId?.let {
                SearchWithSessionUseCase.Type.WithSessionId(
                    query = searchQuery,
                    sessionId = it
                )
            } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)
            callback(Either.Right(searchType), response)
        } catch (error: Exception) {
            callback(Either.Left(error), response)
        }
    }

    override fun sendSearchResponse(
        result: SearchWithSessionUseCase.Result,
        response: FirebaseFunctionsResponse
    ) {
        // TODO: Needs correct mapping
        val jsonResponse = Json.encodeToString(result.toResult())
        response.send(jsonResponse)
    }

    override fun sendSearchErrorResponse(response: FirebaseFunctionsResponse) {
        // TODO: Needs correct mapping
        val jsonResponse = Json.encodeToString(Result.Empty)
        response.send(jsonResponse)
    }
}

@Serializable
private sealed class Result {
    @Serializable
    @SerialName("empty")
    object Empty : Result()

    @Serializable
    @SerialName("valid")
    data class Valid(
        val searchSessionId: String,
        val query: String,
        val postTitle: String,
        val postUrl: String,
        val postImageUrl: String,
        val totalPosts: Int,
    ) : Result()
}

private fun SearchWithSessionUseCase.Result.toResult() = Result.Valid(
    searchSessionId = searchSessionId,
    query = query,
    postTitle = post.title,
    postUrl = post.url,
    postImageUrl = post.imageUrl,
    totalPosts = totalPosts
)