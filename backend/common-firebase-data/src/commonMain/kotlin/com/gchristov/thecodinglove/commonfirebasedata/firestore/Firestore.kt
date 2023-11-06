package com.gchristov.thecodinglove.commonfirebasedata.firestore

interface Firestore {
    fun collection(path: String): FirestoreCollectionReference
}