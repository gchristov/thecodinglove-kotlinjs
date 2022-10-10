package com.gchristov.thecodinglove

import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

external fun require(module:String) : dynamic
external var exports: dynamic

fun main(args: Array<String>) {
    val fireFunctions = require("firebase-functions")
    exports.myTestFun = fireFunctions.https.onRequest { request, response ->
        val client = provideHttpClient()
        GlobalScope.launch {
            val userResponse = client.get<Response>("https://reqres.in/api/users")
            response.send(Messenger().message() + ", " + userResponse.page)
        }
    }
}

private fun provideHttpClient() = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
            ignoreUnknownKeys = true
        })
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