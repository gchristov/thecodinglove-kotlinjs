package com.gchristov.thecodinglove.search

import arrow.core.Either
import arrow.core.flatMap
import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservice.http.BaseHttpHandler
import com.gchristov.thecodinglove.commonservice.http.HttpHandler
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.http.HttpResponse
import com.gchristov.thecodinglove.commonservicedata.http.decodeBodyFromJson
import com.gchristov.thecodinglove.commonservicedata.pubsub2.PubSub
import com.gchristov.thecodinglove.commonservicedata.pubsub2.publish
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SearchHttpHandler(
    private val jsonSerializer: Json,
    private val log: Logger,
    private val searchUseCase: SearchUseCase,
    private val pubSub: PubSub,
) : BaseHttpHandler(
    jsonSerializer = jsonSerializer,
    log = log
) {
    override fun httpConfig() = HttpHandler.Config(
        method = HttpMethod.Get,
        path = "/api/search",
        contentType = ContentType.Application.Json,
    )

    override suspend fun handleAsync(
        request: HttpRequest,
        response: HttpResponse,
    ): Either<Throwable, Unit> {
        println("QUERY=${request.query.get<String>("test")}")
        println("HEADER=${request.headers.get<String>("test")}")

        return request.decodeBodyFromJson<BodyTest>(jsonSerializer)
            .flatMap {
                println("DECODED_BODY=$it")
                pubSub
                    .topic(PreloadSearchPubSubHttpHandler.PubSubTopic)
                    .publish(
                        body = it,
                        jsonSerializer = jsonSerializer,
                    )
            }
            .flatMap { messageId ->
                println("PubSub message sent: id=$messageId")
                super.handleAsync(request, response)
            }
    }
}

@Serializable
data class BodyTest(
    val title: String?,
    val subtitle: String?,
)