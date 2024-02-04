package com.gchristov.thecodinglove.slack.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.common.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.adapter.http.SlackApi
import com.gchristov.thecodinglove.slack.adapter.http.SlackAuthHttpHandler
import com.gchristov.thecodinglove.slack.adapter.http.SlackEventHttpHandler
import com.gchristov.thecodinglove.slack.adapter.http.SlackSlashCommandHttpHandler
import com.gchristov.thecodinglove.slack.adapter.pubsub.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slack.adapter.search.RealSearchEngine
import com.gchristov.thecodinglove.slack.adapter.search.RealSearchSessionStorage
import com.gchristov.thecodinglove.slack.domain.SlackMessageFactory
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.ports.SearchEngine
import com.gchristov.thecodinglove.slack.domain.ports.SearchSessionStorage
import com.gchristov.thecodinglove.slack.domain.ports.SlackAuthStateSerializer
import com.gchristov.thecodinglove.slack.domain.ports.SlackRepository
import com.gchristov.thecodinglove.slack.domain.usecase.SlackAuthUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackRevokeTokensUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackSendSearchUseCase
import com.gchristov.thecodinglove.slack.domain.usecase.SlackVerifyRequestUseCase
import kotlinx.coroutines.Dispatchers
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackAdapterModule : DiModule() {
    override fun name() = "slack-adapter"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSlackApi(client = instance()) }
            bindSingleton { provideSlackConfig() }
            bindSingleton {
                provideSlackRepository(
                    api = instance(),
                    firebaseAdmin = instance(),
                    jsonSerializer = instance(),
                )
            }
            bindProvider {
                provideSlackAuthStateSerializer(jsonSerializer = instance())
            }
            bindSingleton {
                provideSearchSessionStorage(searchRepository = instance())
            }
            bindProvider {
                provideSearchSessionShuffle(searchUseCase = instance())
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
                    searchEngine = instance(),
                    pubSubDecoder = instance(),
                )
            }
        }
    }

    private fun provideSlackApi(client: NetworkClient.Json) = SlackApi(client)

    private fun provideSlackConfig(): SlackConfig = SlackConfig(
        signingSecret = BuildConfig.SLACK_SIGNING_SECRET,
        timestampValidityMinutes = 5,
        requestVerificationEnabled = BuildConfig.SLACK_REQUEST_VERIFICATION_ENABLED,
        clientId = BuildConfig.SLACK_CLIENT_ID,
        clientSecret = BuildConfig.SLACK_CLIENT_SECRET,
        interactivityPubSubTopic = BuildConfig.SLACK_INTERACTIVITY_PUBSUB_TOPIC,
        slashCommandPubSubTopic = BuildConfig.SLACK_SLASH_COMMAND_PUBSUB_TOPIC,
        monitoringUrl = BuildConfig.SLACK_MONITORING_URL,
    )

    private fun provideSlackRepository(
        api: SlackApi,
        firebaseAdmin: FirebaseAdmin,
        jsonSerializer: JsonSerializer.ExplicitNulls,
    ): SlackRepository = RealSlackRepository(
        apiService = api,
        firebaseAdmin = firebaseAdmin,
        jsonSerializer = jsonSerializer,
    )

    private fun provideSlackAuthStateSerializer(jsonSerializer: JsonSerializer.Default): SlackAuthStateSerializer =
        RealSlackAuthStateSerializer(jsonSerializer = jsonSerializer)

    private fun provideSearchSessionStorage(searchRepository: SearchRepository): SearchSessionStorage =
        RealSearchSessionStorage(searchRepository = searchRepository)

    private fun provideSearchSessionShuffle(searchUseCase: SearchUseCase): SearchEngine =
        RealSearchEngine(searchUseCase = searchUseCase)

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
        searchEngine: SearchEngine,
        pubSubDecoder: PubSubDecoder,
    ): SlackSlashCommandPubSubHandler = SlackSlashCommandPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackRepository = slackRepository,
        slackMessageFactory = slackMessageFactory,
        searchEngine = searchEngine,
        pubSubDecoder = pubSubDecoder,
    )
}