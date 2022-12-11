package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.slackdata.SlackSlashCommandRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandService(slackSlashCommandRepository = instance())
            }
        }
    }

    private fun provideSlackSlashCommandService(
        slackSlashCommandRepository: SlackSlashCommandRepository,
    ): SlackSlashCommandService = SlackSlashCommandService(
        slackSlashCommandRepository = slackSlashCommandRepository,
    )
}