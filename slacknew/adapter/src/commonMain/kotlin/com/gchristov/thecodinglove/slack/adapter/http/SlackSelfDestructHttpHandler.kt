package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSelfDestructHttpHandler(
    dispatcher: CoroutineDispatcher,
    jsonSerializer: JsonSerializer,
    log: Logger,
    private val selfDestructUseCase: SlackSelfDestructUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/slack/self-destruct",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = selfDestructUseCase.invoke().flatMap { response.sendEmpty() }
}