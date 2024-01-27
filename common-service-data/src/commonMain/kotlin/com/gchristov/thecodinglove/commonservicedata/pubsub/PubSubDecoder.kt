package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest

interface PubSubDecoder {
    fun decode(request: HttpRequest): Either<Throwable, PubSubRequest>
}