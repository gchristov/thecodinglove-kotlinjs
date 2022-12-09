package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import com.gchristov.thecodinglove.slackdata.SlackDataModule
import com.gchristov.thecodinglove.slackdata.SlackSlashCommandRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandService(slackSlashCommandRepository = inject())
            }
        }
    }

    override fun moduleDependencies(): List<DI.Module> {
        return listOf(SlackDataModule.module)
    }

    private fun provideSlackSlashCommandService(
        slackSlashCommandRepository: SlackSlashCommandRepository,
    ): SlackSlashCommandService = SlackSlashCommandService(
        slackSlashCommandRepository = slackSlashCommandRepository,
    )

    fun injectSlackSlashCommandService(): SlackSlashCommandService = inject()
}