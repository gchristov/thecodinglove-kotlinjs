package com.gchristov.thecodinglove.common.firebase.firestore

import com.gchristov.thecodinglove.common.firebase.GoogleFirebaseAdminExternals
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import kotlinx.coroutines.await
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.encodeToDynamic
import kotlin.js.json

internal class GoogleFirestoreDocumentReference(
    private val js: GoogleFirebaseAdminExternals.firestore.DocumentReference
) : FirestoreDocumentReference {
    override suspend fun get() = safeJsCall("Error getting Firestore document") {
        GoogleFirestoreDocumentSnapshot(js.get().await())
    }

    @OptIn(ExperimentalSerializationApi::class)
    override suspend fun <T> set(
        jsonSerializer: JsonSerializer,
        strategy: SerializationStrategy<T>,
        data: T,
        merge: Boolean,
    ) = safeJsCall("Error setting Firestore document") {
        js.set(jsonSerializer.json.encodeToDynamic(strategy, data), json("merge" to merge)).await()
        Unit
    }

    override suspend fun delete() = safeJsCall("Error deleting Firestore document") {
        js.delete().await()
        Unit
    }
}

internal class GoogleFirestoreDocumentSnapshot(
    private val js: GoogleFirebaseAdminExternals.firestore.DocumentSnapshot
) : FirestoreDocumentSnapshot {
    override val exists: Boolean = js.exists

    override fun data(): dynamic = js.data()
}
