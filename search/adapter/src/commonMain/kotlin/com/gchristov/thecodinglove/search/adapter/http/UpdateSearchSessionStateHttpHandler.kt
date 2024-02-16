package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.search.adapter.http.mapper.toState
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.proto.http.ApiUpdateSearchSessionState
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class UpdateSearchSessionStateHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val searchRepository: SearchRepository,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Put,
        path = "/api/search/session-state",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = request.decodeBodyFromJson(
        jsonSerializer = jsonSerializer,
        strategy = ApiUpdateSearchSessionState.serializer(),
    )
        .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
        .flatMap { sessionUpdate ->
            searchRepository
                .getSearchSession(sessionUpdate.searchSessionId)
                .flatMap {
                    searchRepository.saveSearchSession(it.copy(state = sessionUpdate.state.toState()))
                }
        }
        .flatMap { response.sendEmpty() }
}