package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandService()
            }
        }
    }

    private fun provideSlackSlashCommandService(): SlackSlashCommandService =
        SlackSlashCommandService()
}