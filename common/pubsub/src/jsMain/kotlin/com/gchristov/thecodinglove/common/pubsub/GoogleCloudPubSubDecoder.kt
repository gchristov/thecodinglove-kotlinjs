package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.left
import arrow.core.right
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import kotlin.io.encoding.ExperimentalEncodingApi

internal class GoogleCloudPubSubDecoder(private val jsonSerializer: JsonSerializer) : PubSubDecoder {
    @ExperimentalEncodingApi
    override fun decode(request: HttpRequest): Either<Throwable, PubSubRequest> = request.decodeBodyFromJson(
        jsonSerializer = jsonSerializer,
        strategy = GoogleCloudPubSubRequestBody.serializer(),
    )
        .flatMap { it?.right() ?: Exception("Request body is invalid").left<Throwable>() }
        .flatMap { it.toPubSubRequest() }
}