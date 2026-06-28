package com.gchristov.thecodinglove.slack.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.analytics.Analytics
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.common.slack.SlackSender
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackCancelSearchPubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackInteractivityReceivedPubSubDispatchHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSearchPubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSelfDestructPubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSendPubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackShufflePubSubEventHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSlashCommandReceivedPubSubDispatchHandler
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
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackAdapterModule : DiModule() {
    override fun name() = "slack-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackRepository(
                    slackSender = instance(),
                    firebaseAdmin = instance(),
                    jsonSerializer = instance(),
                )
            }
            bindProvider {
                provideSlackAuthStateSerializer(jsonSerializer = instance())
            }
            bindSingleton {
                provideSlackSearchServiceApi(
                    networkClient = instance(),
                    environment = instance(),
                )
            }
            bindSingleton {
                provideSlackSearchRepository(slackSearchServiceApi = instance())
            }
            bindSingleton {
                provideSlackEventHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackVerifyRequestUseCase = instance(),
                    slackConfig = instance(),
                    slackRevokeTokensUseCase = instance(),
                    analytics = instance(),
                )
            }
            bindSingleton {
                provideSlackAuthHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackAuthUseCase = instance(),
                    slackSendSearchUseCase = instance(),
                    analytics = instance(),
                )
            }
            bindSingleton {
                provideSlackSlashCommandHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackVerifyRequestUseCase = instance(),
                    slackConfig = instance(),
                    pubSubPublisher = instance(),
                )
            }
            bindSingleton {
                provideSlackSlashCommandReceivedPubSubDispatchHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackRepository = instance(),
                    slackMessageFactory = instance(),
                    searchRepository = instance(),
                    pubSubDecoder = instance(),
                    analytics = instance(),
                )
            }
            bindSingleton {
                provideSlackInteractivityHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackVerifyRequestUseCase = instance(),
                    slackConfig = instance(),
                    pubSubPublisher = instance(),
                )
            }
            bindSingleton {
                provideSlackInteractivityReceivedPubSubDispatchHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackSendSearchUseCase = instance(),
                    slackShuffleSearchUseCase = instance(),
                    slackCancelSearchUseCase = instance(),
                    pubSubDecoder = instance(),
                    analytics = instance(),
                )
            }
            bindSingleton {
                provideSlackSelfDestructHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    selfDestructUseCase = instance(),
                )
            }
            bindSingleton {
                provideSlackStatisticsHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    statisticsUseCase = instance(),
                )
            }
        }
    }

    private fun provideSlackRepository(
        slackSender: SlackSender,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SlackRepository = RealSlackRepository(
        slackSender = slackSender,
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    private fun provideSlackAuthStateSerializer(jsonSerializer: JsonSerializer.Default): SlackAuthStateSerializer =
        RealSlackAuthStateSerializer(jsonSerializer = jsonSerializer)

    private fun provideSlackSearchServiceApi(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): SlackSearchServiceApi = SlackSearchServiceApi(
        client = networkClient,
        environment = environment,
    )

    private fun provideSlackSearchRepository(slackSearchServiceApi: SlackSearchServiceApi): SlackSearchRepository =
        RealSlackSearchRepository(slackSearchServiceApi = slackSearchServiceApi)

    private fun provideSlackEventHttpHandler(
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

    private fun provideSlackAuthHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackAuthUseCase: SlackAuthUseCase,
        slackSendSearchUseCase: SlackSendSearchUseCase,
        analytics: Analytics,
    ): SlackAuthHttpHandler = SlackAuthHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackAuthUseCase = slackAuthUseCase,
        slackSendSearchUseCase = slackSendSearchUseCase,
        analytics = analytics,
    )

    private fun provideSlackSlashCommandHttpHandler(
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

    private fun provideSlackSlashCommandReceivedPubSubDispatchHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
        searchRepository: SlackSearchRepository,
        pubSubDecoder: PubSubDecoder,
        analytics: Analytics,
    ): SlackSlashCommandReceivedPubSubDispatchHandler = SlackSlashCommandReceivedPubSubDispatchHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        eventHandlers = listOf(
            SlackSearchPubSubEventHandler(
                slackRepository = slackRepository,
                slackMessageFactory = slackMessageFactory,
                slackSearchRepository = searchRepository,
                analytics = analytics,
            )
        ),
        pubSubDecoder = pubSubDecoder,
    )

    private fun provideSlackInteractivityHttpHandler(
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

    private fun provideSlackInteractivityReceivedPubSubDispatchHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackSendSearchUseCase: SlackSendSearchUseCase,
        slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
        slackCancelSearchUseCase: SlackCancelSearchUseCase,
        pubSubDecoder: PubSubDecoder,
        analytics: Analytics,
    ): SlackInteractivityReceivedPubSubDispatchHandler = SlackInteractivityReceivedPubSubDispatchHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        eventHandlers = listOf(
            SlackSendPubSubEventHandler(slackSendSearchUseCase, analytics),
            SlackSelfDestructPubSubEventHandler(slackSendSearchUseCase, analytics),
            SlackShufflePubSubEventHandler(slackShuffleSearchUseCase, analytics),
            SlackCancelSearchPubSubEventHandler(slackCancelSearchUseCase, analytics),
        ),
        pubSubDecoder = pubSubDecoder,
    )

    private fun provideSlackSelfDestructHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        selfDestructUseCase: SlackSelfDestructUseCase,
    ): SlackSelfDestructHttpHandler = SlackSelfDestructHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        selfDestructUseCase = selfDestructUseCase,
    )

    private fun provideSlackStatisticsHttpHandler(
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
