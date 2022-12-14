package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservice.*
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.searchdata.api.toSearchResult
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchApiService(
    private val pubSub: PubSub,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
) : ApiService() {
    override fun register() {
        exports.search = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ) {
        try {
            val searchQuery: String = request.query["searchQuery"] ?: "release"
            val searchSessionId: String? = request.query["searchSessionId"]
            val searchType = searchSessionId?.let {
                SearchWithSessionUseCase.Type.WithSessionId(
                    query = searchQuery,
                    sessionId = it
                )
            } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)

            search(
                searchType = searchType,
                response = response
            )
        } catch (error: Exception) {
            error.printStackTrace()
            // TODO: Needs correct response mapping
            response.status(400).send("ERROR")
        }
    }

    private suspend fun search(
        searchType: SearchWithSessionUseCase.Type,
        response: ApiResponse
    ) {
        println("Performing search...")
        searchWithSessionUseCase(searchType)
            .fold(
                ifLeft = {
                    it.printStackTrace()
                    // TODO: Needs correct response mapping
                    response.status(400).send("ERROR")
                },
                ifRight = { searchResult ->
                    // TODO: Needs correct response mapping
                    println("Search complete")
                    preload(searchResult.searchSessionId)
                    response.send(Json.encodeToString(searchResult.toSearchResult()))
                }
            )
    }

    private fun preload(searchSessionId: String) {
        println("Preloading next result...")
        val preload = PreloadTopicMessage(searchSessionId = searchSessionId)
        pubSub.topic("trigger").publish(Buffer.from(Json.encodeToString(preload)))
    }
}