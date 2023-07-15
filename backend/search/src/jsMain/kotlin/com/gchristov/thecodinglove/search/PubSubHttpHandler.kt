package com.gchristov.thecodinglove.search

import arrow.core.Either
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservice.HttpRequest
import com.gchristov.thecodinglove.commonservice.HttpResponse
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlinx.serialization.json.Json

class PubSubHttpHandler(
    private val jsonSerializer: Json,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
) : BaseHttpHandler(
    jsonSerializer = jsonSerializer,
    log = log
) {

    override suspend fun handleAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        println("QUERY=${request.query.get<String>("test")}")
        println("HEADER=${request.headers.get<String>("test")}")
        println("BODY_STRING=${request.bodyString}")
        return super.handleAsync(request, response)
    }
}