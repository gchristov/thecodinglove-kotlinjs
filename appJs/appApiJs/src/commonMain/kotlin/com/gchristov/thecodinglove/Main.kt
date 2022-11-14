package com.gchristov.thecodinglove

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseApp
import dev.gitlive.firebase.firestore.firestore
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

external fun require(module:String) : dynamic
external var exports: dynamic

fun main(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        val client = provideHttpClient()
        GlobalScope.launch {
            val userResponse: Response = client.get("https://reqres.in/api/users").body()

            println("About to test Firestore")
            val firebase = provideFirebaseApp()
            val firestore = Firebase.firestore(firebase)
            val document = firestore.document("preferences/user1").get()
            val count = document.get<Int>("count")
            println("Got Firestore document: exists=${document.exists}, count=$count")
            val batch = firestore.batch()
            batch.set(firestore.document("preferences/user1"), Count(count + 1))
            batch.commit()

            response.send(Messenger().message() + ", " + userResponse.page + ", " + count)
        }
    }
}

private fun provideHttpClient() = HttpClient {
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
    install(Logging) {
        logger = Logger.SIMPLE
        level = LogLevel.ALL
    }
}

@Serializable
private data class Response(
    val page: Int
)

@Serializable
private data class Count(
    val count: Int
)