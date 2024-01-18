package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either

interface FirestoreCollectionReference : FirestoreCollectionQuery {
    fun document(path: String): FirestoreDocumentReference
}

interface FirestoreCollectionQuery {
    suspend fun get(): Either<Throwable, FirestoreCollectionQuerySnapshot>

    fun where(
        field: String,
        opStr: String,
        value: Any?,
    ): FirestoreCollectionQuery
}

interface FirestoreCollectionQuerySnapshot {
    val docs: List<FirestoreDocumentSnapshot>
}