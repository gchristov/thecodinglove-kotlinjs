package com.gchristov.thecodinglove.slack.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.ports.SlackRepository
import com.gchristov.thecodinglove.slack.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
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
            bindProvider {
                provideSlackRevokeTokensUseCase(
                    log = instance(),
                    slackRepository = instance(),
                )
            }
            bindProvider {
                provideSelfDestructUseCase(
                    log = instance(),
                    slackRepository = instance(),
                )
            }
            bindProvider {
                provideSlackVerifyRequestUseCase(
                    slackConfig = instance(),
                    log = instance()
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

    private fun provideSlackRevokeTokensUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackRevokeTokensUseCase = RealSlackRevokeTokensUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
    )

    private fun provideSelfDestructUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackSelfDestructUseCase = RealSlackSelfDestructUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
        clock = Clock.System,
    )

    private fun provideSlackVerifyRequestUseCase(
        slackConfig: SlackConfig,
        log: Logger,
    ): SlackVerifyRequestUseCase = RealSlackVerifyRequestUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        clock = Clock.System,
        log = log,
    )
}