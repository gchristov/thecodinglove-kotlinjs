package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.firebase.admin
import com.gchristov.thecodinglove.kmpcommonkotlin.process

class FirebaseAdminTest {
    private var app: admin.App? = null

    fun init() {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-gcp.json"
        app = admin.initializeApp()
    }

    fun testFirestore() {
        admin
            .firestore
            .Firestore()
            .collection("messages")
            .doc("test")
            .get()
            .then(onFulfilled = {
                println("GOT DOCUMENT: ${it.get("something")}")
            }, onRejected = {
                it.printStackTrace()
            })
    }
}