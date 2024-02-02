package com.gchristov.thecodinglove.common.firebase.firestore

import com.gchristov.thecodinglove.common.firebase.GoogleFirebaseAdminExternals

internal class GoogleFirestore(
    private val js: GoogleFirebaseAdminExternals.firestore.Firestore
) : Firestore {
    override fun collection(path: String): FirestoreCollectionReference =
        GoogleFirestoreCollectionReference(js.collection(path))
}