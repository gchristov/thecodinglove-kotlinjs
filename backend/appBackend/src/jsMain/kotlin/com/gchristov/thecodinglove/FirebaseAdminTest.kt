package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonservicedata.TestFirestoreDoc
import com.gchristov.thecodinglove.firebase.admin
import com.gchristov.thecodinglove.firebase.decodeBodyFromJson
import com.gchristov.thecodinglove.firebase.getValue
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import com.gchristov.thecodinglove.kmpcommonkotlin.process
import kotlinx.serialization.json.Json

class FirebaseAdminTest {
    private var app: admin.App? = null

    fun init() {
        process.env.GOOGLE_APPLICATION_CREDENTIALS = "local-credentials-gcp.json"
        app = admin.initializeApp()
    }

    fun testFirestore() {
        val store = admin
            .firestore
            .Firestore()
        store.collection("messages")
            .doc("test")
            .get()
            .then(onFulfilled = {
                println("TYPED ARGUMENT: something=${it.getValue<Int>("something")}")
                println("RAW DOCUMENT: something=${it.data()}")
                val jsonSerializer = DiGraph.inject<Json>()
                it.decodeBodyFromJson(jsonSerializer, TestFirestoreDoc.serializer()).fold(
                    ifLeft = {
                        it.printStackTrace()
                    },
                    ifRight = {
                        println("PARSED DOCUMENT: $it")
                    }
                )
//                store.collection("messages")
//                    .doc("test")
//                    .set(TestFirestoreDoc(something = doc.something + 1))
            }, onRejected = {
                it.printStackTrace()
            })
    }
}