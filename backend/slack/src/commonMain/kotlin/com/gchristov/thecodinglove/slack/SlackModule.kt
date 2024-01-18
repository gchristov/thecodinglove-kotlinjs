package com.gchristov.thecodinglove.slack

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonkotlin.JsonSerializer
import com.gchristov.thecodinglove.commonkotlin.di.DiModule
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubDecoder
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubPublisher
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityHttpHandler
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandHttpHandler
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubHandler
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.*
import kotlinx.coroutines.Dispatchers
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
                    pubSubDecoder = instance(),
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
                    pubSubDecoder = instance(),
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
            bindSingleton {
                provideSelfDestructHttpHandler(
                    jsonSerializer = instance(),
                    log = instance(),
                    slackSelfDestructUseCase = instance(),
                )
            }
        }
    }

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
        searchUseCase: SearchUseCase,
        pubSubDecoder: PubSubDecoder,
        slackConfig: SlackConfig,
    ): SlackSlashCommandPubSubHandler = SlackSlashCommandPubSubHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackRepository = slackRepository,
        searchUseCase = searchUseCase,
        pubSubDecoder = pubSubDecoder,
        slackConfig = slackConfig,
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

    private fun provideSelfDestructHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        slackSelfDestructUseCase: SlackSelfDestructUseCase,
    ): SlackSelfDestructHttpHandler = SlackSelfDestructHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        slackSelfDestructUseCase = slackSelfDestructUseCase,
    )
}