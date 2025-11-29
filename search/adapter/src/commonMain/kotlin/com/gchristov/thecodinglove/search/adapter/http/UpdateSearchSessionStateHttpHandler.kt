package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.search.adapter.http.mapper.toState
import com.gchristov.thecodinglove.search.adapter.http.model.ApiUpdateSearchSessionState
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
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
        ).bind()
        if (body == null) {
            raise(Exception("Request body is invalid"))
        }

        val searchSession = searchRepository.getSearchSession(body.searchSessionId).bind()

        val updatedSearchSession = searchSession.copy(state = body.state.toState())
        searchRepository.saveSearchSession(updatedSearchSession).bind()

        response.sendEmpty().bind()
    }
}