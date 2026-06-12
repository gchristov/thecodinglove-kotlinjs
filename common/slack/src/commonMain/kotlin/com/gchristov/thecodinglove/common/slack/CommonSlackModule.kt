package com.gchristov.thecodinglove.common.slack

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object CommonSlackModule : DiModule() {
    override fun name() = "common-slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSlackSender(networkClient = instance()) }
        }
    }

    private fun provideSlackSender(networkClient: NetworkClient.Json): SlackSender =
        SlackSender(client = networkClient)
}
