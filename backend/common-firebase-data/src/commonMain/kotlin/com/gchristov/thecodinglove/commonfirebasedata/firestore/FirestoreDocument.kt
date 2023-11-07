package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromDynamic

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

    fun data(): dynamic

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> decodeDataFromJson(
        jsonSerializer: Json,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = try {
        if (data() != null) {
            Either.Right(jsonSerializer.decodeFromDynamic(strategy, data()))
        } else {
            Either.Right(null)
        }
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error decoding Firestore document snapshot data${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}