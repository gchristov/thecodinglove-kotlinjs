package com.gchristov.thecodinglove.commonfirebasedata

import com.gchristov.thecodinglove.commonfirebasedata.firestore.Firestore
import com.gchristov.thecodinglove.commonfirebasedata.firestore.GoogleFirestore
import com.gchristov.thecodinglove.kmpcommonkotlin.process

internal class GoogleFirebaseAdmin : FirebaseAdmin {
    private val app: GoogleFirebaseAdminExternals.App

    init {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-gcp.json"
        app = GoogleFirebaseAdminExternals.initializeApp()
    }

    override fun firestore(): Firestore = GoogleFirestore(app.firestore())
}