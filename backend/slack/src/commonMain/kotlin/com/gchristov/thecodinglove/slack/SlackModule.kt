package com.gchristov.thecodinglove.slack

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSubscription
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityHttpHandler
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandHttpHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.*
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
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
                    searchUseCase = instance(),
                    pubSubSubscription = instance(),
                    slackConfig = instance(),
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
                    pubSubSubscription = instance(),
                    slackConfig = instance(),
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
                provideSlackEventHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackVerifyRequestUseCase = instance(),
                    slackConfig = instance(),
                    slackRevokeTokensUseCase = instance(),
                )
            }
        }
    }

    private fun provideSlackSlashCommandHttpHandler(
        jsonSerializer: Json,
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
        jsonSerializer: Json,
        log: Logger,
        slackRepository: SlackRepository,
        searchUseCase: SearchUseCase,
        pubSubSubscription: PubSubSubscription,
        slackConfig: SlackConfig,
    ): SlackSlashCommandPubSubHandler = SlackSlashCommandPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackRepository = slackRepository,
        searchUseCase = searchUseCase,
        pubSubSubscription = pubSubSubscription,
        slackConfig = slackConfig,
    )

    private fun provideSlackInteractivityHttpHandler(
        jsonSerializer: Json,
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
        jsonSerializer: Json,
        log: Logger,
        slackSendSearchUseCase: SlackSendSearchUseCase,
        slackShuffleSearchUseCase: SlackShuffleSearchUseCase,
        slackCancelSearchUseCase: SlackCancelSearchUseCase,
        pubSubSubscription: PubSubSubscription,
        slackConfig: SlackConfig,
    ): SlackInteractivityPubSubHandler = SlackInteractivityPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackSendSearchUseCase = slackSendSearchUseCase,
        slackShuffleSearchUseCase = slackShuffleSearchUseCase,
        slackCancelSearchUseCase = slackCancelSearchUseCase,
        pubSubSubscription = pubSubSubscription,
        slackConfig = slackConfig,
    )

    private fun provideSlackAuthHttpHandler(
        jsonSerializer: Json,
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

    private fun provideSlackEventHttpHandler(
        jsonSerializer: Json,
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
}