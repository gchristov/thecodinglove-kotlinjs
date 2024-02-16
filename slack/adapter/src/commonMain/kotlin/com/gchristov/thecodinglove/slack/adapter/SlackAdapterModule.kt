package com.gchristov.thecodinglove.slack.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.search.proto.http.SearchApiRepository
import com.gchristov.thecodinglove.slack.adapter.http.*
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.search.RealSlackSearchRepository
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

class SlackAdapterModule(private val environment: Environment) : DiModule() {
    override fun name() = "slack-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSlackApi(client = instance()) }
            bindSingleton { provideSlackConfig() }
            bindSingleton {
                provideSlackRepository(
                    slackApi = instance(),
                    firebaseAdmin = instance(),
                    jsonSerializer = instance(),
                )
            }
            bindProvider {
                provideSlackAuthStateSerializer(jsonSerializer = instance())
            }
            bindSingleton {
                provideSlackSearchRepository(searchApiRepository = instance())
            }
            bindSingleton {
                provideSlackEventHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackVerifyRequestUseCase = instance(),
                    slackConfig = instance(),
                    slackRevokeTokensUseCase = instance(),
                )
            }
            bindSingleton {
                provideSlackAuthHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackAuthUseCase = instance(),
                    slackSendSearchUseCase = instance(),
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
                provideSlackSlashCommandPubSubHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackRepository = instance(),
                    slackMessageFactory = instance(),
                    searchRepository = instance(),
                    pubSubDecoder = instance(),
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
                provideSlackInteractivityPubSubHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackSendSearchUseCase = instance(),
                    slackShuffleSearchUseCase = instance(),
                    slackCancelSearchUseCase = instance(),
                    pubSubDecoder = instance(),
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
            bindSingleton {
                provideSlackReportExceptionHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    reportExceptionUseCase = instance(),
                )
            }
        }
    }

    private fun provideSlackApi(client: NetworkClient.Json) = SlackApi(client)

    private fun provideSlackConfig(): SlackConfig = SlackConfig(
        signingSecret = BuildConfig.SLACK_SIGNING_SECRET,
        timestampValidityMinutes = 5,
        requestVerificationEnabled = environment.slackRequestVerification,
        clientId = BuildConfig.SLACK_CLIENT_ID,
        clientSecret = BuildConfig.SLACK_CLIENT_SECRET,
        interactivityPubSubTopic = environment.slackInteractivityPubSubTopic,
        slashCommandPubSubTopic = environment.slackSlashCommandPubSubTopic,
        monitoringUrl = BuildConfig.SLACK_MONITORING_URL,
    )

    private fun provideSlackRepository(
        slackApi: SlackApi,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SlackRepository = RealSlackRepository(
        slackApi = slackApi,
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    private fun provideSlackAuthStateSerializer(jsonSerializer: JsonSerializer.Default): SlackAuthStateSerializer =
        RealSlackAuthStateSerializer(jsonSerializer = jsonSerializer)

    private fun provideSlackSearchRepository(searchApiRepository: SearchApiRepository): SlackSearchRepository =
        RealSlackSearchRepository(searchApiRepository = searchApiRepository)

    private fun provideSlackEventHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackVerifyRequestUseCase: SlackVerifyRequestUseCase,
        slackConfig: SlackConfig,
        slackRevokeTokensUseCase: SlackRevokeTokensUseCase,
    ): SlackEventHttpHandler = SlackEventHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackVerifyRequestUseCase = slackVerifyRequestUseCase,
        slackConfig = slackConfig,
        slackRevokeTokensUseCase = slackRevokeTokensUseCase,
    )

    private fun provideSlackAuthHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackAuthUseCase: SlackAuthUseCase,
        slackSendSearchUseCase: SlackSendSearchUseCase,
    ): SlackAuthHttpHandler = SlackAuthHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackAuthUseCase = slackAuthUseCase,
        slackSendSearchUseCase = slackSendSearchUseCase,
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

    private fun provideSlackSlashCommandPubSubHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackRepository: SlackRepository,
        slackMessageFactory: SlackMessageFactory,
        searchRepository: SlackSearchRepository,
        pubSubDecoder: PubSubDecoder,
    ): SlackSlashCommandPubSubHandler = SlackSlashCommandPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
        slackSearchRepository = searchRepository,
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

    private fun provideSlackInteractivityPubSubHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackSendSearchUseCase: SlackSendSearchUseCase,
        slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
        slackCancelSearchUseCase: SlackCancelSearchUseCase,
        pubSubDecoder: PubSubDecoder,
    ): SlackInteractivityPubSubHandler = SlackInteractivityPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackSendSearchUseCase = slackSendSearchUseCase,
        slackShuffleSearchUseCase = slackShuffleSearchUseCase,
        slackCancelSearchUseCase = slackCancelSearchUseCase,
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

    private fun provideSlackReportExceptionHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        reportExceptionUseCase: SlackReportExceptionUseCase,
    ): SlackReportExceptionHttpHandler = SlackReportExceptionHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        reportExceptionUseCase = reportExceptionUseCase,
    )
}