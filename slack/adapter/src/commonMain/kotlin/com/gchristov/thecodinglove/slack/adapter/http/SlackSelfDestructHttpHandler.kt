package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackSelfDestructHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val selfDestructUseCase: SlackSelfDestructUseCase,
) : HttpHandler {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/slack/self-destruct",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        selfDestructUseCase.invoke().bind()
        response.sendEmpty().bind()
    }
}
