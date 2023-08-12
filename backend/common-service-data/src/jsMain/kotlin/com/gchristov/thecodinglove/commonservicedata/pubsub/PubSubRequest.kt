package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

interface PubSubRequest {
    val bodyString: String?

    fun <T> decodeBodyFromJson(
        jsonSerializer: Json,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?>
}
