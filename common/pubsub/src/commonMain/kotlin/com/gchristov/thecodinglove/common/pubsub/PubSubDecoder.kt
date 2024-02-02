package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.http.HttpRequest

interface PubSubDecoder {
    fun decode(request: HttpRequest): Either<Throwable, PubSubRequest>
}