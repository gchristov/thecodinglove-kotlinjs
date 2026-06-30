package com.gchristov.thecodinglove.common.firebase.firestore

import com.gchristov.thecodinglove.common.firebase.GoogleFirebaseAdminExternals
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import kotlinx.coroutines.await

// Composition instead of inheritance: Kotlin 2.x does not generate interface dispatch bridges for
// internal open classes, so we delegate to a contained query object instead of extending it.
internal class GoogleFirestoreCollectionReference(
    private val js: GoogleFirebaseAdminExternals.firestore.CollectionReference
) : FirestoreCollectionReference {
    private val query = GoogleFirestoreCollectionQuery(js)
    override fun document(path: String): FirestoreDocumentReference = GoogleFirestoreDocumentReference(js.doc(path))
    override suspend fun get() = query.get()
    override fun where(field: String, opStr: String, value: Any?) = query.where(field, opStr, value)
}

internal class GoogleFirestoreCollectionQuery(
    private val js: GoogleFirebaseAdminExternals.firestore.Query
) : FirestoreCollectionQuery {
    override suspend fun get() = safeJsCall("Error getting Firestore collection query") {
        GoogleFirestoreCollectionQuerySnapshot(js.get().await())
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
