package com.gchristov.thecodinglove.common.pubsub

import arrow.core.Either

fun interface PubSubEventHandler<T> {
    suspend fun handle(event: T): Either<Throwable, Unit>
}
