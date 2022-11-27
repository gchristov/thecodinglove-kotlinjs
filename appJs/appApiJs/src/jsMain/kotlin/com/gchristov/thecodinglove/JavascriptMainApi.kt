package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpcommonfirebase.CommonFirebaseModule
import com.gchristov.thecodinglove.kmpsearch.SearchModule
import com.gchristov.thecodinglove.kmpsearch.ShuffleResult
import com.gchristov.thecodinglove.kmpsearch.ShuffleType
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
            println("About to test Firestore")
            val firestore = CommonFirebaseModule.injectFirestore()
            val document = firestore.document("preferences/user1").get()
            val count = document.get<Int>("count")
            println("Got Firestore document: exists=${document.exists}, count=$count")
            val batch = firestore.batch()
            batch.set(firestore.document("preferences/user1"), Count(count + 1))
            batch.commit()

            println("About to test shuffle")
            val shuffle = SearchModule.injectShuffleUseCase()
            val shuffleType = searchSessionId?.let {
                ShuffleType.WithSessionId(
                    query = searchQuery,
                    sessionId = it
                )
            } ?: ShuffleType.NewSearch(searchQuery)
            val shuffleResult = shuffle(
                shuffleType = shuffleType,
                resultsPerPage = 4
            )
            val result = FunctionResult(
                invocations = count,
                searchResult = shuffleResult.toResult()
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
            val postTitle: String,
            val postUrl: String,
            val postImageUrl: String,
            val totalPosts: Int,
        ) : FunctionSearchResult()
    }
}

private fun ShuffleResult.toResult() = when (this) {
    is ShuffleResult.Empty -> FunctionResult.FunctionSearchResult.Empty
    is ShuffleResult.Valid -> FunctionResult.FunctionSearchResult.Valid(
        query = query,
        postTitle = post.title,
        postUrl = post.url,
        postImageUrl = post.imageUrl,
        totalPosts = totalPosts
    )
}