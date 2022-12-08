package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.kmpcommondi.inject
import org.kodein.di.DI
import org.kodein.di.bindProvider

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindLocalDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideSlackSlashCommandService()
            }
        }
    }

    private fun provideSlackSlashCommandService(): SlackSlashCommandService =
        RealSlackSlashCommandService()

    fun injectSlackSlashCommandService(): SlackSlashCommandService = inject()
}