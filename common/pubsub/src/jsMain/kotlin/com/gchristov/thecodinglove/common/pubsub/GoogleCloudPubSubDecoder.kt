package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import arrow.core.raise.either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import kotlin.io.encoding.ExperimentalEncodingApi

internal class GoogleCloudPubSubDecoder(private val jsonSerializer: JsonSerializer) : PubSubDecoder {
    @ExperimentalEncodingApi
    override fun decode(request: HttpRequest): Either<Throwable, PubSubRequest> = either {
        val body = request.decodeBodyFromJson(
            jsonSerializer = jsonSerializer,
            strategy = GoogleCloudPubSubRequestBody.serializer(),
        ).bind()
        if (body == null) {
            raise(Exception("Request body is invalid"))
        }

        body.toPubSubRequest().bind()
    }
}