package com.gchristov.thecodinglove.commonservice.pubsub

import arrow.core.*
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
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