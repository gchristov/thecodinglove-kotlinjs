package com.gchristov.thecodinglove.common.adaptertestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.adapter.http.HttpRequest
import com.gchristov.thecodinglove.common.adapter.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.adapter.pubsub.PubSubRequest

class FakePubSubDecoder(
    private val pubSubRequest: PubSubRequest
) : PubSubDecoder {
    override fun decode(request: HttpRequest): Either<Throwable, PubSubRequest> = Either.Right(pubSubRequest)
}