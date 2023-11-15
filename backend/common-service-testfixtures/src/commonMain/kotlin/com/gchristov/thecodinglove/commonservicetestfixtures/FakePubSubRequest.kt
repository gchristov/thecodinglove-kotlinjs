package com.gchristov.thecodinglove.commonservicetestfixtures

import arrow.core.Either
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubRequest
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
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