package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.Handler

interface PubSubHandler : Handler {
    suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit>
}