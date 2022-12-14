package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandService(jsonSerializer = instance())
            }
        }
    }

    private fun provideSlackSlashCommandService(
        jsonSerializer: Json
    ): SlackSlashCommandApiService = SlackSlashCommandApiService(
        jsonSerializer = jsonSerializer
    )
}