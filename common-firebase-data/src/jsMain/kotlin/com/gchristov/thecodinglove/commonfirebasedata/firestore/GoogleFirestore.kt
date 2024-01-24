package com.gchristov.thecodinglove.commonfirebasedata.firestore

import com.gchristov.thecodinglove.commonfirebasedata.GoogleFirebaseAdminExternals

internal class GoogleFirestore(
    private val js: GoogleFirebaseAdminExternals.firestore.Firestore
) : Firestore {
    override fun collection(path: String): FirestoreCollectionReference =
        GoogleFirestoreCollectionReference(js.collection(path))
}