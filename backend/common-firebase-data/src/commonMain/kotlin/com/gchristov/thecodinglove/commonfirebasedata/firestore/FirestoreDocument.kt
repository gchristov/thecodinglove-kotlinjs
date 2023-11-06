package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json

interface FirestoreDocumentReference {
    suspend fun get(): Either<Throwable, FirestoreDocumentSnapshot>

    suspend fun <T> set(
        jsonSerializer: Json,
        strategy: SerializationStrategy<T>,
        data: T,
    ): Either<Throwable, Unit>

    suspend fun delete(): Either<Throwable, Unit>
}

interface FirestoreDocumentSnapshot {
    val exists: Boolean

    fun data(): Any?

    fun <T> decodeDataFromJson(
        jsonSerializer: Json,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = try {
        Either.Right(data()?.let { jsonSerializer.decodeFromString(strategy, JSON.stringify(it)) })
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error decoding FirestoreDocumentSnapshot data${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}