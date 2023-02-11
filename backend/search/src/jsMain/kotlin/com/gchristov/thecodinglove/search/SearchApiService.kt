package com.gchristov.thecodinglove.search

import arrow.core.Either
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.search.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.searchdata.api.ApiSearchResult
import com.gchristov.thecodinglove.searchdata.api.toPost
import kotlinx.serialization.json.Json

class SearchApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val pubSubSender: PubSubSender,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer
) {
    override fun register() {
        exports.search = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit> = searchWithSessionUseCase(request.toSearchType())
        .map { result ->
            // TODO: Needs correct response mapping
            preload(result.searchSessionId)
            response.sendJson(
                data = result.toSearchResult(),
                jsonSerializer = jsonSerializer
            )
        }

    private fun preload(searchSessionId: String) {
        val preloadPubSubMessage = PreloadPubSubService.buildPubSubMessage(searchSessionId)
        pubSubSender.sendMessage(
            topic = preloadPubSubMessage.topic,
            body = preloadPubSubMessage,
            jsonSerializer = jsonSerializer
        )
    }
}

private fun ApiRequest.toSearchType(): SearchWithSessionUseCase.Type {
    val searchQuery: String = query["searchQuery"] ?: "release"
    val searchSessionId: String? = query["searchSessionId"]
    return searchSessionId?.let {
        SearchWithSessionUseCase.Type.WithSessionId(
            query = searchQuery,
            sessionId = it
        )
    } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)
}

internal fun SearchWithSessionUseCase.Result.toSearchResult() = ApiSearchResult(
    searchSessionId = searchSessionId,
    query = query,
    post = post.toPost(),
    totalPosts = totalPosts
)