package com.gchristov.thecodinglove.slack.domain

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.slack.domain.model.Environment
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.port.SlackAuthStateSerializer
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SlackDomainComponent {
    @Provides
    @Singleton
    fun provideSlackConfig(environment: Environment): SlackConfig = SlackConfig(
        signingSecret = BuildConfig.SLACK_SIGNING_SECRET,
        timestampValidityMinutes = 5,
        requestVerificationEnabled = environment.slackRequestVerification,
        clientId = BuildConfig.SLACK_CLIENT_ID,
        clientSecret = BuildConfig.SLACK_CLIENT_SECRET,
        interactivityReceivedPubSubTopic = environment.slackInteractivityReceivedPubSubTopic,
        slashCommandReceivedPubSubTopic = environment.slackSlashCommandReceivedPubSubTopic,
        selfDestructMessagePubSubTopic = environment.slackSelfDestructMessagePubSubTopic,
    )

    @Provides
    fun provideSlackMessageFactory(
        slackAuthStateSerializer: SlackAuthStateSerializer,
    ): SlackMessageFactory = RealSlackMessageFactory(
        slackAuthStateSerializer = slackAuthStateSerializer,
    )

    @Provides
    fun provideSlackAuthUseCase(
        slackConfig: SlackConfig,
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackAuthUseCase = RealSlackAuthUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        log = log,
        slackRepository = slackRepository,
    )

    @Provides
    fun provideSlackRevokeTokensUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackRevokeTokensUseCase = RealSlackRevokeTokensUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
    )

    @Provides
    fun provideSelfDestructUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackSelfDestructUseCase = RealSlackSelfDestructUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
    )

    @OptIn(ExperimentalTime::class)
    @Provides
    fun provideSlackVerifyRequestUseCase(
        slackConfig: SlackConfig,
        log: Logger,
    ): SlackVerifyRequestUseCase = RealSlackVerifyRequestUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        clock = Clock.System,
        log = log,
    )

    @Provides
    fun provideSlackCancelSearchUseCase(
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

    @Provides
    fun provideSlackEnsureAuthenticatedUseCase(
        log: Logger,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
        slackConfig: SlackConfig,
    ): SlackEnsureAuthenticatedUseCase = RealSlackEnsureAuthenticatedUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
        slackConfig = slackConfig,
    )

    @OptIn(ExperimentalTime::class)
    @Provides
    fun provideSlackSendSearchUseCase(
        log: Logger,
        slackSearchRepository: SlackSearchRepository,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
    ): SlackSendSearchUseCase = RealSlackSendSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackSearchRepository = slackSearchRepository,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
        clock = Clock.System,
    )

    @Provides
    fun provideSlackShuffleSearchUseCase(
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

    @Provides
    fun provideSlackStatisticsUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackStatisticsUseCase = RealSlackStatisticsUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
    )
}
