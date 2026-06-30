package com.gchristov.thecodinglove.common.firebase.firestore

import com.gchristov.thecodinglove.common.firebase.GoogleFirebaseAdminExternals
import com.gchristov.thecodinglove.common.kotlin.safeJsCall
import kotlinx.coroutines.await

internal class GoogleFirestoreCollectionReference(
    private val js: GoogleFirebaseAdminExternals.firestore.CollectionReference
) : FirestoreCollectionReference, GoogleFirestoreCollectionQuery(js) {
    override fun document(path: String): FirestoreDocumentReference = GoogleFirestoreDocumentReference(js.doc(path))
}

internal open class GoogleFirestoreCollectionQuery(
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
