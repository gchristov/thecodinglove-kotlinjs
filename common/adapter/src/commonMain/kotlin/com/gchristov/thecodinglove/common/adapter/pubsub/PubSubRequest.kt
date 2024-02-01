package com.gchristov.thecodinglove.common.adapter.pubsub

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import kotlinx.serialization.DeserializationStrategy

interface PubSubRequest {
    val bodyString: String?

    fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?>
}
