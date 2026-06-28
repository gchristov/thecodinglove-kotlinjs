package com.gchristov.thecodinglove.search.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.search.domain.port.SearchRepository
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class DeleteSearchSessionHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val searchRepository: SearchRepository,
) : HttpHandler {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Delete,
        path = "/api/search/session",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        searchRepository.deleteSearchSession(requireNotNull(request.query["searchSessionId"])).bind()
        response.sendEmpty().bind()
    }
}
