package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpcommonfirebase.CommonFirebaseModule
import com.gchristov.thecodinglove.kmpsearch.SearchHistory
import com.gchristov.thecodinglove.kmpsearch.SearchModule
import com.gchristov.thecodinglove.kmpsearch.SearchResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal actual fun serveApi(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        val searchQuery = (request.query.searchQuery as? String) ?: "release"
        // TODO: Do not use GlobalScope
        GlobalScope.launch {
            println("About to test Firestore")
            val firestore = CommonFirebaseModule.injectFirestore()
            val document = firestore.document("preferences/user1").get()
            val count = document.get<Int>("count")
            println("Got Firestore document: exists=${document.exists}, count=$count")
            val batch = firestore.batch()
            batch.set(firestore.document("preferences/user1"), Count(count + 1))
            batch.commit()

            println("About to test search")
            val search = SearchModule.injectSearchUseCase()
            val searchResult = search(
                query = searchQuery,
                searchHistory = SearchHistory(),
                resultsPerPage = 4
            )
            val result = FunctionResult(
                invocations = count,
                searchResult = searchResult.toResult()
            )
            val jsonResponse = Json.encodeToString(result)
            response.send(jsonResponse)
        }
    }
}

private external fun require(module: String): dynamic
private external var exports: dynamic

@Serializable
private data class Count(val count: Int)

@Serializable
private data class FunctionResult(
    val invocations: Int,
    val searchResult: FunctionSearchResult
) {
    @Serializable
    sealed class FunctionSearchResult {
        @Serializable
        object Empty : FunctionSearchResult()

        @Serializable
        data class Valid(
            val query: String,
            val totalPosts: Int,
            val postTitle: String,
            val postUrl: String,
            val postImageUrl: String,
            val postPage: Int,
            val postIndexOnPage: Int,
            val postPageSize: Int
        ) : FunctionSearchResult()
    }
}

private fun SearchResult.toResult() = when (this) {
    is SearchResult.Empty -> FunctionResult.FunctionSearchResult.Empty
    is SearchResult.Valid -> FunctionResult.FunctionSearchResult.Valid(
        query = query,
        totalPosts = totalPosts,
        postTitle = post.title,
        postUrl = post.url,
        postImageUrl = post.imageUrl,
        postPage = postPage,
        postIndexOnPage = postIndexOnPage,
        postPageSize = postPageSize,
    )
}