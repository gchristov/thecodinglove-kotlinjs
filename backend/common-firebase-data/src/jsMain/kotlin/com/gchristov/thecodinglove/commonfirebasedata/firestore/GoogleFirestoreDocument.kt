package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebasedata.GoogleFirebaseAdminExternals
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToDynamic

internal class GoogleFirestoreDocumentReference(
    private val js: GoogleFirebaseAdminExternals.firestore.DocumentReference
) : FirestoreDocumentReference {
    override suspend fun get(): Either<Throwable, FirestoreDocumentSnapshot> = try {
        val result = js.get().await()
        Either.Right(GoogleFirestoreDocumentSnapshot(result))
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error getting Firestore document ${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> set(
        jsonSerializer: Json,
        strategy: SerializationStrategy<T>,
        data: T
    ): Either<Throwable, Unit> = try {
        js.set(jsonSerializer.encodeToDynamic(strategy, data) as Any).await()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error setting Firestore document ${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override suspend fun delete(): Either<Throwable, Unit> = try {
        js.delete().await()
        Either.Right(Unit)
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error deleting Firestore document ${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}

internal class GoogleFirestoreDocumentSnapshot(
    private val js: GoogleFirebaseAdminExternals.firestore.DocumentSnapshot
) : FirestoreDocumentSnapshot {
    override val exists: Boolean = js.exists

    override fun data(): Any? = js.data()
}