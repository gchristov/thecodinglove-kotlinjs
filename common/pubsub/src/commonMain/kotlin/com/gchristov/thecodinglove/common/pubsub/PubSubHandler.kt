package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.network.http.Handler

interface PubSubHandler : Handler {
    suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit>
}