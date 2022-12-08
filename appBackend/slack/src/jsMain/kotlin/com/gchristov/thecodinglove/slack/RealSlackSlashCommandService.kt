package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonfirebase.FirebaseFunctions
import com.gchristov.thecodinglove.commonfirebase.bodyFromJson
import com.gchristov.thecodinglove.commonfirebase.get
import com.gchristov.thecodinglove.kmpcommonkotlin.exports
import com.gchristov.thecodinglove.kmpcommonnetwork.CommonNetworkModule
import com.gchristov.thecodinglove.search.SearchModule
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slackdata.api.ApiSlackSlashCommand
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal class RealSlackSlashCommandService : SlackSlashCommandService {
    override fun register() {
        exports.myTestFun = FirebaseFunctions.https.onRequest { request, response ->
            val searchQuery: String = request.query["searchQuery"] ?: "release"
            val searchSessionId: String? = request.query["searchSessionId"]
            val test: ApiSlackSlashCommand? =
                request.body.bodyFromJson(CommonNetworkModule.injectJsonParser())
            println(test)

            // TODO: Do not use GlobalScope
            GlobalScope.launch {
                println("Performing search")
                val search = SearchModule.injectSearchWithSessionUseCase()
                val searchType = searchSessionId?.let {
                    SearchWithSessionUseCase.Type.WithSessionId(
                        query = searchQuery,
                        sessionId = it
                    )
                } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)
                search(searchType)
                    .fold(
                        ifLeft = {
                            it.printStackTrace()
                            val jsonResponse = Json.encodeToString(Result.Empty)
                            response.send(jsonResponse)
                        },
                        ifRight = { searchResult ->
                            val jsonResponse = Json.encodeToString(searchResult.toResult())
                            response.send(jsonResponse)
                            println("Preloading next result")
                            val preload = SearchModule.injectPreloadSearchResultUseCase()
                            preload(searchResult.searchSessionId)
                                .fold(
                                    ifLeft = { it.printStackTrace() },
                                    ifRight = { println("Preload complete") }
                                )
                        }
                    )
            }
        }
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