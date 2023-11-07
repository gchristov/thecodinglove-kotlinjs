package com.gchristov.thecodinglove.commonfirebasedata.firestore

import com.gchristov.thecodinglove.commonfirebasedata.GoogleFirebaseAdminExternals

internal class GoogleFirestoreCollectionReference(
    private val js: GoogleFirebaseAdminExternals.firestore.CollectionReference
) : FirestoreCollectionReference {
    override fun document(path: String): FirestoreDocumentReference = GoogleFirestoreDocumentReference(js.doc(path))
}