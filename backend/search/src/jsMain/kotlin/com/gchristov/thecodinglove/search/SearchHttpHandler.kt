package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.BaseHttpHandler
import com.gchristov.thecodinglove.commonservice.HttpRequest
import com.gchristov.thecodinglove.commonservice.HttpResponse
import com.gchristov.thecodinglove.commonservice.decodeBodyFromJson
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SearchHttpHandler(
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
        return request.decodeBodyFromJson<BodyTest>(jsonSerializer, log).flatMap {
            println("DECODED_BODY=$it")
            super.handleAsync(request, response)
        }
    }
}

@Serializable
data class BodyTest(
    val title: String?,
    val subtitle: String?,
)