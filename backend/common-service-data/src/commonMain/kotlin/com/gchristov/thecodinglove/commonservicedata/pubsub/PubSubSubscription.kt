package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either

interface PubSubSubscription {
    suspend fun initialise(
        topic: String,
        httpPath: String,
    ): Either<Throwable, Unit>
}