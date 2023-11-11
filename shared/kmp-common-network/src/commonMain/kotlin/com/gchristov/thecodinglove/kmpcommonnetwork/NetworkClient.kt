package com.gchristov.thecodinglove.kmpcommonnetwork

import com.gchristov.thecodinglove.kmpcommonkotlin.AppConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.JsonSerializer
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*

sealed class NetworkClient {
    abstract val http: HttpClient

    data class Html(val appConfig: AppConfig) : NetworkClient() {
        override val http: HttpClient = buildHttpClient(appConfig.networkHtmlLogLevel).config {
            BrowserUserAgent()
        }
    }

    data class Json(
        val appConfig: AppConfig,
        val jsonSerializer: JsonSerializer,
    ) : NetworkClient() {
        override val http: HttpClient = buildHttpClient(appConfig.networkJsonLogLevel).config {
            install(ContentNegotiation) {
                json(jsonSerializer.json)
            }
        }
    }
}

private fun buildHttpClient(logLevel: String) = HttpClient {
    install(Logging) {
        logger = Logger.SIMPLE
        level = when (logLevel) {
            "all" -> LogLevel.ALL
            "info" -> LogLevel.INFO
            else -> LogLevel.NONE
        }
    }
}