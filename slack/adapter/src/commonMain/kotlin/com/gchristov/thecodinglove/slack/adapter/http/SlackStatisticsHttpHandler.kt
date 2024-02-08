package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toStatistics
import com.gchristov.thecodinglove.slack.domain.usecase.SlackStatisticsUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackStatisticsHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val statisticsUseCase: SlackStatisticsUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/slack/statistics",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = statisticsUseCase.invoke().flatMap { statistics ->
        response.sendJson(
            data = statistics.toStatistics(),
            jsonSerializer = jsonSerializer,
        )
    }
}