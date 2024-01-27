package com.gchristov.thecodinglove.commonnetwork

import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonNetworkModule : DiModule() {
    override fun name() = "common-network"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideHtmlNetworkClient() }
            bindSingleton {
                provideJsonNetworkClient(
                    jsonSerializer = instance(),
                )
            }
        }
    }

    private fun provideHtmlNetworkClient() = NetworkClient.Html

    private fun provideJsonNetworkClient(
        jsonSerializer: JsonSerializer.Default,
    ) = NetworkClient.Json(
        jsonSerializer = jsonSerializer,
    )
}