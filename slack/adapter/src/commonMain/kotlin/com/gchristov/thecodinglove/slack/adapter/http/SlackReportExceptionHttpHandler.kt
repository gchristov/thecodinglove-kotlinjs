package com.gchristov.thecodinglove.slack.adapter.http

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.*
import com.gchristov.thecodinglove.slack.domain.usecase.SlackReportExceptionUseCase
import com.gchristov.thecodinglove.slack.proto.http.model.ApiSlackReportException
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SlackReportExceptionHttpHandler(
    dispatcher: CoroutineDispatcher,
    private val jsonSerializer: JsonSerializer,
    log: Logger,
    private val reportExceptionUseCase: SlackReportExceptionUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Post,
        path = "/api/slack/report-exception",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = request.decodeBodyFromJson(
        jsonSerializer = jsonSerializer,
        strategy = ApiSlackReportException.serializer(),
    )
        .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
        .flatMap {
            reportExceptionUseCase(
                SlackReportExceptionUseCase.Dto(
                    message = it.message,
                    stacktrace = it.stacktrace,
                )
            )
        }
        .flatMap { response.sendEmpty() }
}