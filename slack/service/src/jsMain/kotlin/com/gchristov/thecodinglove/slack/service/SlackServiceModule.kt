package com.gchristov.thecodinglove.slack.service

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slack.domain.model.Environment
import org.kodein.di.DI
import org.kodein.di.bindSingleton

class SlackServiceModule(private val environment: Environment) : DiModule() {
    override fun name() = "slack-service"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { environment }
        }
    }
}