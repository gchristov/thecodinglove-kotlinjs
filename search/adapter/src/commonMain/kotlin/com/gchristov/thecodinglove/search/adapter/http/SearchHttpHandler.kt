package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.error
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.search.adapter.http.mapper.toSearchResult
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import com.gchristov.thecodinglove.search.adapter.pubsub.model.SearchSessionResultCreatedEvent
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val searchUseCase: SearchUseCase,
    private val pubSubPublisher: PubSubPublisher,
    private val searchConfig: SearchConfig,
    private val analytics: Analytics,
) : HttpHandler {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/search",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = searchUseCase(SearchUseCase.Dto(request.toSearchType()))
        .fold(
            ifLeft = {
                when (it) {
                    is SearchUseCase.Error.Empty -> {
                        // Empty search results are expected so we respond with 200 and the relevant error type.
                        log.error(tag, it) { "Error handling request" }
                        response.sendJson(
                            data = it.toSearchResult(),
                            jsonSerializer = jsonSerializer,
                        )
                    }

                    is SearchUseCase.Error.SessionNotFound -> Either.Left(it)
                }
            },
            ifRight = { searchResult ->
                analytics.sendEvent(
                    clientId = searchResult.searchSessionId,
                    name = "search",
                    params = mapOf(
                        "query" to searchResult.query,
                        "total_posts" to searchResult.totalPosts.toString(),
                    ),
                )
                either {
                    publishSearchPreloadMessage(
                        searchSessionId = searchResult.searchSessionId,
                        searchConfig = searchConfig,
                    ).bind()
                    response.sendJson(data = searchResult.toSearchResult(), jsonSerializer = jsonSerializer).bind()
                }
            }
        )

    private suspend fun publishSearchPreloadMessage(
        searchSessionId: String,
        searchConfig: SearchConfig,
    ) = pubSubPublisher.publishJson(
        topic = searchConfig.searchSessionResultCreatedPubSubTopic,
        body = SearchSessionResultCreatedEvent(searchSessionId),
        jsonSerializer = jsonSerializer,
        strategy = SearchSessionResultCreatedEvent.serializer(),
    )
}

private fun HttpRequest.toSearchType(): SearchUseCase.Type {
    val searchQuery: String = query["searchQuery"] ?: "release"
    val searchSessionId: String? = query["searchSessionId"]
    return searchSessionId?.let {
        SearchUseCase.Type.WithSessionId(sessionId = it)
    } ?: SearchUseCase.Type.NewSession(searchQuery)
}
