package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackDataModule : DiModule() {
    override fun name() = "slack-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandRepository(json = instance())
            }
        }
    }

    private fun provideSlackSlashCommandRepository(json: Json): SlackSlashCommandRepository =
        RealSlackSlashCommandRepository(jsonParser = json)
}