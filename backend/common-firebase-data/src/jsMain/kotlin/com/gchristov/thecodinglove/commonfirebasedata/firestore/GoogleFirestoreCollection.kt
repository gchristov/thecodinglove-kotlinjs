package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either
import com.gchristov.thecodinglove.commonfirebasedata.GoogleFirebaseAdminExternals
import kotlinx.coroutines.await

internal class GoogleFirestoreCollectionReference(
    private val js: GoogleFirebaseAdminExternals.firestore.CollectionReference
) : FirestoreCollectionReference {
    override fun document(path: String): FirestoreDocumentReference = GoogleFirestoreDocumentReference(js.doc(path))

    override suspend fun add(data: Any): Either<Throwable, FirestoreDocumentReference> = try {
        val result = js.add(data).await()
        Either.Right(GoogleFirestoreDocumentReference(result))
    } catch (error: Throwable) {
        Either.Left(Throwable(
            message = "Error adding Firestore document to collection${error.message?.let { ": $it" } ?: ""}",
            cause = error,
        ))
    }
}