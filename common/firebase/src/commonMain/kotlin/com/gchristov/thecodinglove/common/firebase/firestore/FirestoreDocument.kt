package com.gchristov.thecodinglove.common.firebase.firestore

import arrow.core.Either
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.decodeFromDynamic

interface FirestoreDocumentReference {
    suspend fun get(): Either<Throwable, FirestoreDocumentSnapshot>

    suspend fun <T> set(
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        data: T,
        merge: Boolean = false,
    ): Either<Throwable, Unit>

    suspend fun delete(): Either<Throwable, Unit>
}

interface FirestoreDocumentSnapshot {
    val exists: Boolean

    fun data(): dynamic

    @OptIn(ExperimentalSerializationApi::class)
    fun <T> decodeDataFromJson(
        jsonSerializer: JsonSerializer,
        strategy: DeserializationStrategy<T>
    ): Either<Throwable, T?> = try {
        if (data() != null) {
            Either.Right(jsonSerializer.json.decodeFromDynamic(strategy, data()))
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