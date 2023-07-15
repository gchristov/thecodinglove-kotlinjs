package com.gchristov.thecodinglove.commonservice

import arrow.core.Either
import com.gchristov.thecodinglove.kmpcommonkotlin.Buffer

interface PubSub {
    fun topic(name: String): PubSubTopic
}

interface PubSubTopic {
    suspend fun publishMessage(message: Buffer): Either<Throwable, String>

    suspend fun exists(): Either<Throwable, Boolean>

    suspend fun create(): Either<Throwable, Unit>

    fun subscription(name: String): PubSubSubscription
}

interface PubSubSubscription {
    suspend fun exists(): Either<Throwable, Boolean>

    suspend fun create(pushEndpoint: String): Either<Throwable, Unit>
}