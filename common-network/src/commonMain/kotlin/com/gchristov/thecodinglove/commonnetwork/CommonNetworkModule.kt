package com.gchristov.thecodinglove.commonnetwork

import com.gchristov.thecodinglove.commonkotlin.AppConfig
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonNetworkModule : DiModule() {
    override fun name() = "common-network"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideHtmlNetworkClient(appConfig = instance()) }
            bindSingleton {
                provideJsonNetworkClient(
                    jsonSerializer = instance(),
                    appConfig = instance(),
                )
            }
        }
    }

    private fun provideHtmlNetworkClient(appConfig: AppConfig) = NetworkClient.Html(appConfig)

    private fun provideJsonNetworkClient(
        jsonSerializer: JsonSerializer.Default,
        appConfig: AppConfig,
    ) = NetworkClient.Json(
        appConfig = appConfig,
        jsonSerializer = jsonSerializer,
    )
}