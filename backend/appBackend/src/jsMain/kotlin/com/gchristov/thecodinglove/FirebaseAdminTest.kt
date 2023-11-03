package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservicedata.TestFirestoreDoc
import com.gchristov.thecodinglove.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.firebase.decodeBodyFromJson
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import kotlinx.serialization.json.Json
import kotlin.js.json

class FirebaseAdminTest {
    private var app: FirebaseAdmin.App? = null

    fun init() {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-gcp.json"
        app = FirebaseAdmin.initializeApp()
    }

    fun testFirestore() {
        val doc = FirebaseAdmin
            .firestore
            .Firestore()
            .collection("messages")
            .doc("test")
        doc.get()
            .then(onFulfilled = {
                println("EXISTS: ${it.exists}")
                println("RAW DOCUMENT: something=${it.get("something")}")
                println("RAW DATA: ${it.data()}")
                val jsonSerializer = DiGraph.inject<Json>()
                it.decodeBodyFromJson(jsonSerializer, TestFirestoreDoc.serializer()).fold(
                    ifLeft = {
                        it.printStackTrace()
                    },
                    ifRight = {
                        println("PARSED DOCUMENT: $it")
                        doc.update(json("something" to it!!.something + 1))
                    }
                )
            }, onRejected = {
                it.printStackTrace()
            })
    }
}