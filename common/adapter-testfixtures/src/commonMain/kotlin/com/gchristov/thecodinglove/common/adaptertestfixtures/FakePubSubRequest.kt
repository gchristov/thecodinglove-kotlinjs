package com.gchristov.thecodinglove.common.adaptertestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.adapter.pubsub.PubSubRequest
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

class FakePubSubRequest<T>(
    private val message: T?,
    private val messageSerializer: SerializationStrategy<T>?,
) : PubSubRequest {
    override val bodyString: String? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeBodyFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = Either.Right(message as T)
}