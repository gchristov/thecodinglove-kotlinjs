package com.gchristov.thecodinglove.commonfirebasedata

import com.gchristov.thecodinglove.commonfirebasedata.firestore.Firestore

interface FirebaseAdmin {
    fun firestore(): Firestore
}