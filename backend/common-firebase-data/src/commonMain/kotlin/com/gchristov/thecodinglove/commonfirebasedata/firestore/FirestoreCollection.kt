package com.gchristov.thecodinglove.commonfirebasedata.firestore

interface FirestoreCollectionReference {
    fun document(path: String): FirestoreDocumentReference
}