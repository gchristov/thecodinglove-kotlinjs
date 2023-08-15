package com.gchristov.thecodinglove.kmpcommonnetwork

import com.gchristov.thecodinglove.kmpcommonkotlin.AppConfig
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
            bindSingleton { provideHtmlClient(appConfig = instance()) }
            bindSingleton {
                provideJsonClient(
                    serializer = instance(),
                    appConfig = instance(),
                )
            }
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun provideJsonSerializer(): Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private fun provideHtmlClient(appConfig: AppConfig) = HtmlClient(
        provideHttpClient(appConfig.networkHtmlLogLevel).config {
            BrowserUserAgent()
        }
    )

    private fun provideJsonClient(
        serializer: Json,
        appConfig: AppConfig,
    ) = JsonClient(
        provideHttpClient(appConfig.networkJsonLogLevel).config {
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