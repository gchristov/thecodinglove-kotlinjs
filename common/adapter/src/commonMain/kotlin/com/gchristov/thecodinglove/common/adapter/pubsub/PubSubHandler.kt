package com.gchristov.thecodinglove.common.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.adapter.Handler

interface PubSubHandler : Handler {
    suspend fun handlePubSubRequest(request: PubSubRequest): Either<Throwable, Unit>
}