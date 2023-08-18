package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.http.HttpRequest
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest

class FakePubSubDecoder(
    private val pubSubRequest: PubSubRequest
) : PubSubDecoder {
    override fun decode(request: HttpRequest): Either<Throwable, PubSubRequest> = Either.Right(pubSubRequest)
}