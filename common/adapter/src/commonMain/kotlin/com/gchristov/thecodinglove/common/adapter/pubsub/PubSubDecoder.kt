package com.gchristov.thecodinglove.common.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.adapter.http.HttpRequest

interface PubSubDecoder {
    fun decode(request: HttpRequest): Either<Throwable, PubSubRequest>
}