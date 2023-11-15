package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebasedata.GoogleFirebaseAdminExternals
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.json

internal class GoogleFirestoreDocumentReference(
    private val js: GoogleFirebaseAdminExternals.firestore.DocumentReference
) : FirestoreDocumentReference {
    override suspend fun get(): Either<Throwable, FirestoreDocumentSnapshot> = try {
        val result = js.get().await()
        Either.Right(GoogleFirestoreDocumentSnapshot(result))
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error getting Firestore document${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> set(
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        data: T,
        merge: Boolean,
    ): Either<Throwable, Unit> = try {
        js.set(jsonSerializer.json.encodeToDynamic(strategy, data), json("merge" to merge)).await()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error setting Firestore document${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun delete(): Either<Throwable, Unit> = try {
        js.delete().await()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error deleting Firestore document${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

internal class GoogleFirestoreDocumentSnapshot(
    private val js: GoogleFirebaseAdminExternals.firestore.DocumentSnapshot
) : FirestoreDocumentSnapshot {
    override val exists: Boolean = js.exists

    override fun data(): dynamic = js.data()
}