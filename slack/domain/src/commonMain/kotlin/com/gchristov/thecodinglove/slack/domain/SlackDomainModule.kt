package com.gchristov.thecodinglove.slack.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.port.SlackAuthStateSerializer
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
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
                provideSlackMessageFactory(slackAuthStateSerializer = instance())
            }
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
            bindProvider {
                provideSlackCancelSearchUseCase(
                    log = instance(),
                    slackSearchRepository = instance(),
                    slackRepository = instance(),
                    slackMessageFactory = instance(),
                )
            }
            bindProvider {
                provideSlackSendSearchUseCase(
                    log = instance(),
                    slackSearchRepository = instance(),
                    slackRepository = instance(),
                    slackConfig = instance(),
                    slackMessageFactory = instance(),
                )
            }
            bindProvider {
                provideSlackShuffleSearchUseCase(
                    log = instance(),
                    slackSearchRepository = instance(),
                    slackRepository = instance(),
                    slackMessageFactory = instance(),
                )
            }
            bindProvider {
                provideStatisticsUseCase(
                    log = instance(),
                    slackRepository = instance(),
                )
            }
        }
    }

    private fun provideSlackMessageFactory(slackAuthStateSerializer: SlackAuthStateSerializer): SlackMessageFactory =
        RealSlackMessageFactory(slackAuthStateSerializer = slackAuthStateSerializer)

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

    private fun provideSlackCancelSearchUseCase(
        log: Logger,
        slackSearchRepository: SlackSearchRepository,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
    ): SlackCancelSearchUseCase = RealSlackCancelSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackSearchRepository = slackSearchRepository,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
    )

    private fun provideSlackSendSearchUseCase(
        log: Logger,
        slackSearchRepository: SlackSearchRepository,
        slackRepository: SlackRepository,
        slackConfig: SlackConfig,
        slackMessageFactory: SlackMessageFactory,
    ): SlackSendSearchUseCase = RealSlackSendSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackSearchRepository = slackSearchRepository,
        slackRepository = slackRepository,
        slackConfig = slackConfig,
        slackMessageFactory = slackMessageFactory,
        clock = Clock.System,
    )

    private fun provideSlackShuffleSearchUseCase(
        log: Logger,
        slackSearchRepository: SlackSearchRepository,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
    ): SlackShuffleSearchUseCase = RealSlackShuffleSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackSearchRepository = slackSearchRepository,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
    )

    private fun provideStatisticsUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackStatisticsUseCase = RealSlackStatisticsUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
    )
}