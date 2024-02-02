package com.gchristov.thecodinglove.common.pubsubtestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.pubsub.PubSubRequest
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