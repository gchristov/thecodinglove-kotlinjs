package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommonnetwork.CommonNetworkModule
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackDataModule : DiModule() {
    override fun name() = "slack-data"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandRepository(json = instance())
            }
        }
    }

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(CommonNetworkModule.module)
    }

    private fun provideSlackSlashCommandRepository(json: Json): SlackSlashCommandRepository =
        RealSlackSlashCommandRepository(jsonParser = json)
}