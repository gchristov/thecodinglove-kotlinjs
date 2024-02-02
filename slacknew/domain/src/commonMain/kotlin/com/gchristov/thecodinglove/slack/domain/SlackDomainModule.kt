package com.gchristov.thecodinglove.slack.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.ports.SlackRepository
import com.gchristov.thecodinglove.slack.domain.usecase.RealSlackAuthUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.instance

object SlackDomainModule : DiModule() {
    override fun name() = "slack-domain"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider {
                provideSlackAuthUseCase(
                    slackConfig = instance(),
                    log = instance(),
                    slackRepository = instance(),
                )
            }
        }
    }

    private fun provideSlackAuthUseCase(
        slackConfig: SlackConfig,
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackAuthUseCase = RealSlackAuthUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        log = log,
        slackRepository = slackRepository,
    )
}