package com.gchristov.thecodinglove.search

import com.gchristov.thecodinglove.commonservice.*
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer
import com.gchristov.thecodinglove.searchdata.api.toSearchResult
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SearchApiService(
    private val jsonSerializer: Json,
    private val pubSub: PubSub,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
) : ApiService(jsonSerializer) {
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
            sendError(
                error = error,
                response = response
            )
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
                    sendError(
                        error = it,
                        response = response
                    )
                },
                ifRight = { searchResult ->
                    // TODO: Needs correct response mapping
                    println("Search complete")
                    preload(searchResult.searchSessionId)
                    response.sendJson(data = jsonSerializer.encodeToString(searchResult.toSearchResult()))
                }
            )
    }

    private fun preload(searchSessionId: String) {
        println("Preloading next result...")
        val preload = PreloadPubSubService.buildTopicMessage(searchSessionId)
        pubSub.topic(preload.topic).publish(Buffer.from(jsonSerializer.encodeToString(preload)))
    }
}