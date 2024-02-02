package com.gchristov.thecodinglove.common.pubsubtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.http.HttpRequest
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest

class FakePubSubDecoder(
    private val pubSubRequest: PubSubRequest
) : PubSubDecoder {
    override fun decode(request: HttpRequest): Either<Throwable, PubSubRequest> = Either.Right(pubSubRequest)
}