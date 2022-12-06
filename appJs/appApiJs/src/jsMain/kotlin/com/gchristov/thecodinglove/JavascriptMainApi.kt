package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.kmpsearchdata.usecase.SearchWithSessionUseCase
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal actual fun serveApi(args: Array<String>) {
    exports.myTestFun = FirebaseFunctions.https.onRequest { request, response ->
        println("Hello")
        response.send("World!")
//        val searchQuery = (request.query.searchQuery as? String) ?: "release"
//        val searchSessionId = request.query.searchSessionId as? String
//        val test = request.body.channel_name as? String
//        println(test)
//
//        // TODO: Do not use GlobalScope
//        GlobalScope.launch {
//            println("Performing normal search")
//            val search = SearchModule.injectSearchWithSessionUseCase()
//            val searchType = searchSessionId?.let {
//                SearchWithSessionUseCase.Type.WithSessionId(
//                    query = searchQuery,
//                    sessionId = it
//                )
//            } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)
//            search(searchType)
//                .fold(
//                    ifLeft = {
//                        it.printStackTrace()
//                        val jsonResponse = Json.encodeToString(Result.Empty)
//                        response.send(jsonResponse)
//                    },
//                    ifRight = { searchResult ->
//                        val jsonResponse = Json.encodeToString(searchResult.toResult())
//                        response.send(jsonResponse)
//                        println("Preloading next result")
//                        val preload = SearchModule.injectPreloadSearchResultUseCase()
//                        preload(searchResult.searchSessionId)
//                            .fold(
//                                ifLeft = { it.printStackTrace() },
//                                ifRight = { println("Preload complete") }
//                            )
//                    }
//                )
//        }
    }
}

@JsModule("firebase-functions")
@JsNonModule
external object FirebaseFunctions {
    var https: Https
}

external object Https {
    fun onRequest(callback: (req: Request, res: Response) -> Unit)
}

external class Request

external class Response {
    fun send(data: String)
}

@Serializable
sealed class Result {
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