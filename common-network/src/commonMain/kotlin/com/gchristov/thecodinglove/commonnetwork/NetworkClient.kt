package com.gchristov.thecodinglove.commonnetwork

import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

sealed class NetworkClient {
    abstract val http: HttpClient

    object Html : NetworkClient() {
        override val http: HttpClient = buildHttpClient(LogLevel.INFO).config {
            BrowserUserAgent()
        }
    }

    data class Json(
        val jsonSerializer: JsonSerializer,
    ) : NetworkClient() {
        override val http: HttpClient = buildHttpClient(LogLevel.ALL).config {
            install(ContentNegotiation) {
                json(jsonSerializer.json)
            }
        }
    }
}

private fun buildHttpClient(logLevel: LogLevel) = HttpClient {
    install(Logging) {
        logger = Logger.SIMPLE
        level = logLevel
    }
}