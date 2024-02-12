package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.search.adapter.http.mapper.toSearchSessionPost
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SearchSessionPostHttpHandler(
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
        method = HttpMethod.Get,
        path = "/api/search/session-post",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = searchRepository
        .getSearchSession(requireNotNull(request.query["searchSessionId"]))
        .flatMap { searchSession ->
            response.sendJson(
                data = searchSession.toSearchSessionPost(),
                jsonSerializer = jsonSerializer,
            )
        }
}