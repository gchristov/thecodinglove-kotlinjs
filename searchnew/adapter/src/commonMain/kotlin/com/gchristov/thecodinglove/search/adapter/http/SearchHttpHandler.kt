package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.search.adapter.http.mapper.toPost
import com.gchristov.thecodinglove.search.adapter.http.model.ApiSearchResult
import com.gchristov.thecodinglove.search.adapter.pubsub.PreloadSearchPubSubMessage
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.model.SearchError
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val searchUseCase: SearchUseCase,
    private val pubSubPublisher: PubSubPublisher,
    private val searchConfig: SearchConfig,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/search",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = searchUseCase(SearchUseCase.Dto(request.toSearchType()))
        .flatMap { searchResult ->
            publishSearchPreloadMessage(
                searchSessionId = searchResult.searchSessionId,
                searchConfig = searchConfig,
            )
                // Trigger preload before we send the response and propagate the search result.
                .flatMap { Either.Right(searchResult) }
        }
        .flatMap { searchResult ->
            response.sendJson(
                data = searchResult.toSearchResult(),
                jsonSerializer = jsonSerializer,
            )
        }

    private suspend fun publishSearchPreloadMessage(
        searchSessionId: String,
        searchConfig: SearchConfig,
    ) = pubSubPublisher
        .publishJson(
            topic = searchConfig.preloadPubSubTopic,
            body = PreloadSearchPubSubMessage(searchSessionId),
            jsonSerializer = jsonSerializer,
            strategy = PreloadSearchPubSubMessage.serializer(),
        )
        .mapLeft { SearchError.SessionNotFound(additionalInfo = it.message) }
}

private fun HttpRequest.toSearchType(): SearchUseCase.Type {
    val searchQuery: String = query["searchQuery"] ?: "release"
    val searchSessionId: String? = query["searchSessionId"]
    return searchSessionId?.let {
        SearchUseCase.Type.WithSessionId(sessionId = it)
    } ?: SearchUseCase.Type.NewSession(searchQuery)
}

private fun SearchUseCase.Result.toSearchResult() = ApiSearchResult(
    searchSessionId = searchSessionId,
    query = query,
    post = post.toPost(),
    totalPosts = totalPosts
)