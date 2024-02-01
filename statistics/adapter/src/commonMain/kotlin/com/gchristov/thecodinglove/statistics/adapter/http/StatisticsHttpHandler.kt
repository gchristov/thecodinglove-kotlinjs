package com.gchristov.thecodinglove.statistics.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.adapter.http.*
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.statistics.core.usecase.StatisticsReportUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class StatisticsHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val statisticsReportUseCase: StatisticsReportUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/stats",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = statisticsReportUseCase().flatMap { report ->
        response.sendJson(
            data = ApiStatisticsReport.of(report),
            jsonSerializer = jsonSerializer,
        )
    }
}