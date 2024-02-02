package com.gchristov.thecodinglove.common.firebase

import com.gchristov.thecodinglove.common.firebase.firestore.Firestore
import com.gchristov.thecodinglove.common.firebase.firestore.GoogleFirestore
import com.gchristov.thecodinglove.common.kotlin.process

internal class GoogleFirebaseAdmin : FirebaseAdmin {
    private val app: GoogleFirebaseAdminExternals.App

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "credentials-gcp-app.json"
        app = GoogleFirebaseAdminExternals.initializeApp()
    }

    override fun firestore(): Firestore = GoogleFirestore(app.firestore())
}