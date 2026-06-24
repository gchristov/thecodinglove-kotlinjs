package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.search.adapter.http.mapper.toState
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import com.gchristov.thecodinglove.search.adapter.http.model.ApiUpdateSearchSessionState
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
    ): Either<Throwable, Unit> = either {
        val body = request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = ApiUpdateSearchSessionState.serializer(),
        ).bind() ?: raise(Exception("Request body is invalid"))
        val session = searchRepository.getSearchSession(body.searchSessionId).bind()
        searchRepository.saveSearchSession(session.copy(state = body.state.toState())).bind()
        response.sendEmpty().bind()
    }
}