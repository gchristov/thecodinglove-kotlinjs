package com.gchristov.thecodinglove.common.firebase

import com.gchristov.thecodinglove.common.firebase.firestore.Firestore

interface FirebaseAdmin {
    fun firestore(): Firestore
}