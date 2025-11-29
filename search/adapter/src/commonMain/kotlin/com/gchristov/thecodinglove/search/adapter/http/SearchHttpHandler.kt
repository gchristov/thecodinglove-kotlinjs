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
import com.gchristov.thecodinglove.search.adapter.pubsub.model.PubSubPreloadSearchMessage
import com.gchristov.thecodinglove.search.domain.model.SearchConfig
import com.gchristov.thecodinglove.search.domain.usecase.SearchUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
    private val pubSubPublisher: PubSubPublisher,
    private val searchConfig: SearchConfig,
    private val analytics: Analytics,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log
) {
    private val tag = this::class.simpleName

    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/search",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        val searchType = request.toSearchType()
        val searchRes = searchUseCase(SearchUseCase.Dto(searchType)).bind()

        if (searchRes is SearchUseCase.Result.Empty) {
            // Temporary track the error to manually spot-check the logic works as expected.
            log.error(tag, Throwable()) { "Error handling request" }
        }

        if (searchRes is SearchUseCase.Result.Data) {
            analytics.sendEvent(
                clientId = searchRes.searchSessionId,
                name = "search",
                params = mapOf(
                    "query" to searchRes.query,
                    "total_posts" to searchRes.totalPosts.toString(),
                ),
            )

            publishSearchPreloadMessage(
                searchSessionId = searchRes.searchSessionId,
                searchConfig = searchConfig,
            ).bind()
        }

        response.sendJson(
            data = searchRes.toSearchResult(),
            jsonSerializer = jsonSerializer,
        ).bind()
    }

    private suspend fun publishSearchPreloadMessage(
        searchSessionId: String,
        searchConfig: SearchConfig,
    ) = pubSubPublisher.publishJson(
        topic = searchConfig.preloadPubSubTopic,
        body = PubSubPreloadSearchMessage(searchSessionId),
        jsonSerializer = jsonSerializer,
        strategy = PubSubPreloadSearchMessage.serializer(),
    )
}

private fun HttpRequest.toSearchType(): SearchUseCase.Type {
    val searchQuery: String = query["searchQuery"] ?: "release"
    val searchSessionId: String? = query["searchSessionId"]
    return searchSessionId?.let {
        SearchUseCase.Type.WithSessionId(sessionId = it)
    } ?: SearchUseCase.Type.NewSession(searchQuery)
}