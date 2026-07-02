package com.gchristov.thecodinglove.slack.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.common.slack.SlackSender
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.*
import com.gchristov.thecodinglove.slack.adapter.search.RealSlackSearchRepository
import com.gchristov.thecodinglove.slack.adapter.search.SlackSearchServiceApi
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.Environment
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.port.SlackAuthStateSerializer
import com.gchristov.thecodinglove.slack.domain.port.SlackRepository
import com.gchristov.thecodinglove.slack.domain.port.SlackSearchRepository
import com.gchristov.thecodinglove.slack.domain.usecase.*
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SlackAdapterComponent {
    @Provides
    @Singleton
    fun provideSlackRepository(
        slackSender: SlackSender,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SlackRepository = RealSlackRepository(
        slackSender = slackSender,
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    @Provides
    fun provideSlackAuthStateSerializer(
        jsonSerializer: JsonSerializer.Default,
    ): SlackAuthStateSerializer = RealSlackAuthStateSerializer(
        jsonSerializer = jsonSerializer,
    )

    @Provides
    @Singleton
    fun provideSlackSearchRepository(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): SlackSearchRepository = RealSlackSearchRepository(
        slackSearchServiceApi = SlackSearchServiceApi(
            client = networkClient,
            environment = environment,
        ),
    )

    @Provides
    @Singleton
    fun provideSlackEventHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
        slackConfig: SlackConfig,
        slackRevokeTokensUseCase: SlackRevokeTokensUseCase,
        analytics: Analytics,
    ): SlackEventHttpHandler = SlackEventHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackVerifyRequestUseCase = slackVerifyRequestUseCase,
        slackConfig = slackConfig,
        slackRevokeTokensUseCase = slackRevokeTokensUseCase,
        analytics = analytics,
    )

    @Provides
    @Singleton
    fun provideSlackAuthHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackAuthUseCase: SlackAuthUseCase,
        slackSendSearchUseCase: SlackSendSearchUseCase,
        pubSubPublisher: PubSubPublisher,
        slackConfig: SlackConfig,
        analytics: Analytics,
    ): SlackAuthHttpHandler = SlackAuthHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackAuthUseCase = slackAuthUseCase,
        slackSendSearchUseCase = slackSendSearchUseCase,
        pubSubPublisher = pubSubPublisher,
        slackConfig = slackConfig,
        analytics = analytics,
    )

    @Provides
    @Singleton
    fun provideSlackSlashCommandHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
        slackConfig: SlackConfig,
        pubSubPublisher: PubSubPublisher,
    ): SlackSlashCommandHttpHandler = SlackSlashCommandHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackVerifyRequestUseCase = slackVerifyRequestUseCase,
        slackConfig = slackConfig,
        pubSubPublisher = pubSubPublisher,
    )

    @Provides
    @Singleton
    fun provideSlackSearchPubSubHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
        slackSearchRepository: SlackSearchRepository,
        pubSubDecoder: PubSubDecoder,
        analytics: Analytics,
    ): SlackSearchPubSubHandler = SlackSearchPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        pubSubDecoder = pubSubDecoder,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
        slackSearchRepository = slackSearchRepository,
        analytics = analytics,
    )

    @Provides
    @Singleton
    fun provideSlackInteractivityHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
        slackConfig: SlackConfig,
        pubSubPublisher: PubSubPublisher,
    ): SlackInteractivityHttpHandler = SlackInteractivityHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackVerifyRequestUseCase = slackVerifyRequestUseCase,
        slackConfig = slackConfig,
        pubSubPublisher = pubSubPublisher,
    )

    @Provides
    @Singleton
    fun provideSlackInteractivityPubSubHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackEnsureAuthenticatedUseCase: SlackEnsureAuthenticatedUseCase,
        slackSendSearchUseCase: SlackSendSearchUseCase,
        slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
        slackCancelSearchUseCase: SlackCancelSearchUseCase,
        pubSubDecoder: PubSubDecoder,
        pubSubPublisher: PubSubPublisher,
        slackConfig: SlackConfig,
        analytics: Analytics,
    ): SlackInteractivityPubSubHandler = SlackInteractivityPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        eventHandlers = listOf(
            SlackSendInteractivityPubSubHandler(
                slackEnsureAuthenticatedUseCase = slackEnsureAuthenticatedUseCase,
                slackSendSearchUseCase = slackSendSearchUseCase,
                analytics = analytics,
            ),
            SlackSelfDestructInteractivityPubSubHandler(
                jsonSerializer = jsonSerializer,
                slackEnsureAuthenticatedUseCase = slackEnsureAuthenticatedUseCase,
                slackSendSearchUseCase = slackSendSearchUseCase,
                pubSubPublisher = pubSubPublisher,
                slackConfig = slackConfig,
                analytics = analytics,
            ),
            SlackShuffleInteractivityPubSubHandler(slackShuffleSearchUseCase, analytics),
            SlackCancelSearchInteractivityPubSubHandler(slackCancelSearchUseCase, analytics),
        ),
        pubSubDecoder = pubSubDecoder,
    )

    @Provides
    @Singleton
    fun provideSlackSelfDestructMessagePubSubHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        pubSubDecoder: PubSubDecoder,
        selfDestructUseCase: SlackSelfDestructUseCase,
    ): SlackSelfDestructMessagePubSubHandler = SlackSelfDestructMessagePubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        pubSubDecoder = pubSubDecoder,
        selfDestructUseCase = selfDestructUseCase,
    )

    @Provides
    @Singleton
    fun provideSlackStatisticsHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        statisticsUseCase: SlackStatisticsUseCase,
    ): SlackStatisticsHttpHandler = SlackStatisticsHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        statisticsUseCase = statisticsUseCase,
    )
}
