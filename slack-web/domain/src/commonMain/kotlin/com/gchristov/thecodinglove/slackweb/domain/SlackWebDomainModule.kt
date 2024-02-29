package com.gchristov.thecodinglove.slackweb.domain

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slackweb.domain.model.SlackConfig
import org.kodein.di.DI
import org.kodein.di.bindSingleton

object SlackWebDomainModule : DiModule() {
    override fun name() = "slack-web-domain"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSlackConfig() }
        }
    }

    private fun provideSlackConfig(): SlackConfig = SlackConfig(
        clientId = BuildConfig.SLACK_CLIENT_ID,
    )
}