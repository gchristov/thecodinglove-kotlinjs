package com.gchristov.thecodinglove.kmpcommonnetwork

import com.gchristov.thecodinglove.kmpcommonkotlin.AppConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.JsonSerializer
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object KmpCommonNetworkModule : DiModule() {
    override fun name() = "kmp-common-network"

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