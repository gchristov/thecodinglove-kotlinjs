package com.gchristov.thecodinglove.commonservicedata.pubsub

import arrow.core.Either
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

interface PubSubMessage {
    val json: String?
}

inline fun <reified T> PubSubMessage.bodyAsJson(
    jsonSerializer: Json
): Either<Throwable, T?> = try {
    Either.Right(json?.let { jsonSerializer.decodeFromString(it) })
} catch (error: Throwable) {
    Either.Left(error)
}

internal fun FirebaseFunctionsPubSubMessage.toPubSubMessage() = object : PubSubMessage {
    override val json: String? = if (this@toPubSubMessage.json != null) {
        JSON.stringify(this@toPubSubMessage.json)
    } else {
        null
    }
}