package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.raise.either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.slack.adapter.http.mapper.toStatistics
import com.gchristov.thecodinglove.slack.domain.usecase.SlackStatisticsUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackStatisticsHttpHandler(
    override val dispatcher: CoroutineDispatcher,
    override val jsonSerializer: JsonSerializer,
    override val log: Logger,
    private val statisticsUseCase: SlackStatisticsUseCase,
) : HttpHandler {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/slack/statistics",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = either {
        val statistics = statisticsUseCase.invoke().bind()
        response.sendJson(data = statistics.toStatistics(), jsonSerializer = jsonSerializer).bind()
    }
}
