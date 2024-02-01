package com.gchristov.thecodinglove.common.firebase.firestore

interface Firestore {
    fun collection(path: String): FirestoreCollectionReference
}