package com.gchristov.thecodinglove.commonfirebasedata.firestore

import arrow.core.Either

interface FirestoreCollectionReference {
    fun document(path: String): FirestoreDocumentReference

    suspend fun add(data: Any): Either<Throwable, FirestoreDocumentReference>
}