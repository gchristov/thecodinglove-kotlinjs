package com.gchristov.thecodinglove.kmpcommonnetwork

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonNetworkModule : DiModule() {
    override fun name() = "kmp-common-network"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideJsonParser() }
            bindSingleton { provideHttpClient(jsonParser = instance()) }
        }
    }

    private fun provideJsonParser(): Json = Json {
        ignoreUnknownKeys = true
    }

    private fun provideHttpClient(jsonParser: Json) = HttpClient {
        install(ContentNegotiation) {
            json(jsonParser)
        }
        install(Logging) {
            logger = Logger.SIMPLE
            level = LogLevel.INFO
        }
    }
}