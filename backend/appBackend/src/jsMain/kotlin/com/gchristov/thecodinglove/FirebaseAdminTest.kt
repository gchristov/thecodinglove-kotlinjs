package com.gchristov.thecodinglove

import com.gchristov.thecodinglove.commonfirebasedata.FirebaseAdmin
import com.gchristov.thecodinglove.commonfirebasedata.firestore.TestFirestoreDoc
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiGraph
import com.gchristov.thecodinglove.kmpcommonkotlin.di.inject
import kotlinx.serialization.json.Json

class FirebaseAdminTest {
    suspend fun testFirestore() {
        val firebase = DiGraph.inject<FirebaseAdmin>()
        val jsonSerializer = DiGraph.inject<Json>()
        val documentReference = firebase
            .firestore()
            .collection("messages")
            .document("test")
        println("Fetching document with Firebase Admin SDK")
        documentReference.get().fold(
            ifLeft = {
                it.printStackTrace()
            },
            ifRight = { documentSnapshot ->
                println("EXISTS: ${documentSnapshot.exists}")
                println("RAW DATA: ${documentSnapshot.data()}")
                documentSnapshot.decodeDataFromJson(jsonSerializer, TestFirestoreDoc.serializer()).fold(
                    ifLeft = {
                        it.printStackTrace()
                    },
                    ifRight = { document ->
                        println("PARSED DATA: $document")
                        val newDocument = document!!.copy(something = document.something + 1, another = listOf("1", "2"), map = mapOf("3" to "4"))
                        documentReference.set(jsonSerializer, TestFirestoreDoc.serializer(), newDocument).fold(
                            ifLeft = {
                                it.printStackTrace()
                            },
                            ifRight = {
                                println("Document updated")
                            }
                        )
                    }
                )
            }
        )
    }
}