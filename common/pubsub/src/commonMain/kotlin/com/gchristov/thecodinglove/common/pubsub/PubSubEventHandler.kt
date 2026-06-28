package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either

interface PubSubEventHandler<T> {
    fun canHandle(event: T): Boolean
    suspend fun handle(event: T): Either<Throwable, Unit>
}
