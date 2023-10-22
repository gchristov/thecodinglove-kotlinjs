package com.gchristov.thecodinglove.firebase

import arrow.core.Either
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

external interface DocumentSnapshot {
    val exists: Boolean
    val ref: DocumentReference
    fun get(field: String): Any
    fun data(): Any?
}

@Suppress("UNCHECKED_CAST")
fun <T : Any> DocumentSnapshot.getValue(field: String) = get(field) as T

fun <T> DocumentSnapshot.decodeBodyFromJson(
    jsonSerializer: Json,
    strategy: DeserializationStrategy<T>
): Either<Throwable, T?> = try {
    Either.Right(data()?.let { jsonSerializer.decodeFromString(strategy, JSON.stringify(it)) })
} catch (error: Throwable) {
    Either.Left(Throwable(
        message = "Error decoding DocumentSnapshot data${error.message?.let { ": $it" } ?: ""}",
        cause = error,
    ))
}