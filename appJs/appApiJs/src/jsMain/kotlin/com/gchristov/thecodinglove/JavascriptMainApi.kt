package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpsearch.SearchModule
import com.gchristov.thecodinglove.kmpsearch.SearchType
import com.gchristov.thecodinglove.kmpsearch.SearchWithSessionUseCase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal actual fun serveApi(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        val searchQuery = (request.query.searchQuery as? String) ?: "release"
        val searchSessionId = request.query.searchSessionId as? String

        // TODO: Do not use GlobalScope
        GlobalScope.launch {
            val search = SearchModule.injectSearchWithSessionUseCase()
            val searchType = searchSessionId?.let {
                SearchType.WithSessionId(
                    query = searchQuery,
                    sessionId = it
                )
            } ?: SearchType.NewSearch(searchQuery)
            val searchResult = search(
                searchType = searchType,
                resultsPerPage = 4
            )
            val result = searchResult.toResult()
            val jsonResponse = Json.encodeToString(result)
            response.send(jsonResponse)
        }
    }
}

private external fun require(module: String): dynamic
private external var exports: dynamic

@Serializable
sealed class Result {
    @Serializable
    object Empty : Result()

    @Serializable
    data class Valid(
        val query: String,
        val postTitle: String,
        val postUrl: String,
        val postImageUrl: String,
        val totalPosts: Int,
    ) : Result()
}

private fun SearchWithSessionUseCase.Result.toResult() = when (this) {
    is SearchWithSessionUseCase.Result.Empty -> Result.Empty
    is SearchWithSessionUseCase.Result.Valid -> Result.Valid(
        query = query,
        postTitle = post.title,
        postUrl = post.url,
        postImageUrl = post.imageUrl,
        totalPosts = totalPosts
    )
}