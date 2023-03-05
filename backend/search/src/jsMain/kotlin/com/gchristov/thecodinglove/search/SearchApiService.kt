package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.ApiService
import com.gchristov.thecodinglove.commonservicedata.api.ApiRequest
import com.gchristov.thecodinglove.commonservicedata.api.ApiResponse
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.api.sendJson
import com.gchristov.thecodinglove.commonservicedata.exports
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.sendMessage
import com.gchristov.thecodinglove.searchdata.api.ApiSearchResult
import com.gchristov.thecodinglove.searchdata.api.toPost
import com.gchristov.thecodinglove.searchdata.model.PreloadPubSubMessage
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import kotlinx.serialization.json.Json

class SearchApiService(
    apiServiceRegister: ApiServiceRegister,
    private val jsonSerializer: Json,
    private val log: Logger,
    private val pubSubSender: PubSubSender,
    private val searchWithSessionUseCase: SearchWithSessionUseCase,
) : ApiService(
    apiServiceRegister = apiServiceRegister,
    jsonSerializer = jsonSerializer,
    log = log
) {
    override fun register() {
        exports.search = registerForApiCallbacks()
    }

    override suspend fun handleRequest(
        request: ApiRequest,
        response: ApiResponse
    ): Either<Throwable, Unit> = searchWithSessionUseCase(request.toSearchType())
        .flatMap { searchResult ->
            publishPreloadMessage(searchResult.searchSessionId)
                .flatMap {
                    // TODO: Needs correct response mapping
                    response.sendJson(
                        data = searchResult.toSearchResult(),
                        jsonSerializer = jsonSerializer,
                        log = log
                    )
                }
        }

    private suspend fun publishPreloadMessage(searchSessionId: String) = pubSubSender.sendMessage(
        topic = PreloadPubSubService.Topic,
        body = PreloadPubSubMessage(searchSessionId),
        jsonSerializer = jsonSerializer,
        log = log
    )
}

private fun ApiRequest.toSearchType(): SearchWithSessionUseCase.Type {
    val searchQuery: String = query["searchQuery"] ?: "release"
    val searchSessionId: String? = query["searchSessionId"]
    return searchSessionId?.let {
        SearchWithSessionUseCase.Type.WithSessionId(sessionId = it)
    } ?: SearchWithSessionUseCase.Type.NewSession(searchQuery)
}

private fun SearchWithSessionUseCase.Result.toSearchResult() = ApiSearchResult(
    searchSessionId = searchSessionId,
    query = query,
    post = post.toPost(),
    totalPosts = totalPosts
)