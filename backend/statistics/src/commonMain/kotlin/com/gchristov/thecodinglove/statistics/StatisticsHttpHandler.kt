package com.gchristov.thecodinglove.statistics

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendJson
import com.gchristov.thecodinglove.statisticsdata.api.toStatisticsReport
import com.gchristov.thecodinglove.statisticsdata.usecase.StatisticsReportUseCase
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
            data = report.toStatisticsReport(),
            jsonSerializer = jsonSerializer,
        )
    }
}