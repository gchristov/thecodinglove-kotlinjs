package com.gchristov.thecodinglove.kmpcommonnetwork

import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object KmpCommonNetworkModule : DiModule() {
    override fun name() = "kmp-common-network"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideJsonSerializer() }
            bindSingleton { provideHtmlClient() }
            bindSingleton { provideJsonClient(serializer = instance()) }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun provideJsonSerializer(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private fun provideHtmlClient() = HtmlClient(
        provideHttpClient(BuildKonfig.APP_NETWORK_HTML_LOG_LEVEL).config {
            BrowserUserAgent()
        }
    )

    private fun provideJsonClient(serializer: Json) = JsonClient(
        provideHttpClient(BuildKonfig.APP_NETWORK_JSON_LOG_LEVEL).config {
            install(ContentNegotiation) {
                json(serializer)
            }
        }
    )

    private fun provideHttpClient(logLevel: String) = HttpClient {
        install(Logging) {
            logger = Logger.SIMPLE
            level = when (logLevel) {
                "all" -> LogLevel.ALL
                "info" -> LogLevel.INFO
                else -> LogLevel.NONE
            }
        }
    }
}