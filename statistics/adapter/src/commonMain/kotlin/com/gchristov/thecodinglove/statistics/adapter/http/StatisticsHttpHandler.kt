package com.gchristov.thecodinglove.statistics.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.benasher44.uuid.uuid4
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.statistics.adapter.http.mapper.toStatisticsReport
import com.gchristov.thecodinglove.statistics.domain.usecase.StatisticsReportUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class StatisticsHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val statisticsReportUseCase: StatisticsReportUseCase,
    private val analytics: Analytics,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/statistics",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        analytics.sendEvent(clientId = uuid4().toString(), name = "api_statistics")
        return statisticsReportUseCase().flatMap { report ->
            response.sendJson(
                data = report.toStatisticsReport(),
                jsonSerializer = jsonSerializer,
            )
        }
    }
}