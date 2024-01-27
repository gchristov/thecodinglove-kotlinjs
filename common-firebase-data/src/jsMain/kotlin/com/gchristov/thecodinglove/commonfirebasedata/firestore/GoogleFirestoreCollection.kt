package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebasedata.GoogleFirebaseAdminExternals
import kotlinx.coroutines.await

internal class GoogleFirestoreCollectionReference(
    private val js: GoogleFirebaseAdminExternals.firestore.CollectionReference
) : FirestoreCollectionReference, GoogleFirestoreCollectionQuery(js) {
    override fun document(path: String): FirestoreDocumentReference = GoogleFirestoreDocumentReference(js.doc(path))
}

internal open class GoogleFirestoreCollectionQuery(
    private val js: GoogleFirebaseAdminExternals.firestore.Query
) : FirestoreCollectionQuery {
    override suspend fun get(): Either<Throwable, FirestoreCollectionQuerySnapshot> = try {
        val result = js.get().await()
        Either.Right(GoogleFirestoreCollectionQuerySnapshot(result))
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error getting Firestore collection query${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }

    override fun where(
        field: String,
        opStr: String,
        value: Any?,
    ): FirestoreCollectionQuery = GoogleFirestoreCollectionQuery(js.where(field, opStr, value))
}

internal class GoogleFirestoreCollectionQuerySnapshot(
    js: GoogleFirebaseAdminExternals.firestore.QuerySnapshot
) : FirestoreCollectionQuerySnapshot {
    override val docs: List<FirestoreDocumentSnapshot> = js.docs.map { GoogleFirestoreDocumentSnapshot(it) }
}