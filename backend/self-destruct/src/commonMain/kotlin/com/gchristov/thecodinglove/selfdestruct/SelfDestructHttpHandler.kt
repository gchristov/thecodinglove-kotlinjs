package com.gchristov.thecodinglove.selfdestruct

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.sendEmpty
import com.gchristov.thecodinglove.slackdata.usecase.SlackSelfDestructUseCase
import io.ktor.http.*
import kotlinx.coroutines.CoroutineDispatcher

class SelfDestructHttpHandler(
    dispatcher: CoroutineDispatcher,
    jsonSerializer: JsonSerializer,
    log: Logger,
    private val slackSelfDestructUseCase: SlackSelfDestructUseCase,
) : BaseHttpHandler(
    dispatcher = dispatcher,
    jsonSerializer = jsonSerializer,
    log = log,
) {
    override fun httpConfig() = HttpHandler.HttpConfig(
        method = HttpMethod.Get,
        path = "/api/self-destruct",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleHttpRequestAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> = slackSelfDestructUseCase().flatMap { response.sendEmpty() }
}