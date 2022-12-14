package com.gchristov.thecodinglove.kmpcommonnetwork

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object KmpCommonNetworkModule : DiModule() {
    override fun name() = "kmp-common-network"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideJsonSerializer() }
            bindSingleton { provideHttpClient(jsonSerializer = instance()) }
        }
    }

    private fun provideJsonSerializer(): Json = Json {
        ignoreUnknownKeys = true
    }

    private fun provideHttpClient(jsonSerializer: Json) = HttpClient {
        BrowserUserAgent()
        install(ContentNegotiation) {
            json(jsonSerializer)
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
    }
}