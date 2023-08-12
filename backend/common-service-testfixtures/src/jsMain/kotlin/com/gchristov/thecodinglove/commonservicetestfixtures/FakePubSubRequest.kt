package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

class FakePubSubRequest<T>(
    private val message: T?,
    private val messageSerializer: SerializationStrategy<T>?,
) : PubSubRequest {
    override val bodyString: String? = null

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeBodyFromJson(
        jsonSerializer: Json,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = Either.Right(message as T)
}