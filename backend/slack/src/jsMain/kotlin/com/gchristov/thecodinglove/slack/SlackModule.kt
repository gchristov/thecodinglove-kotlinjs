package com.gchristov.thecodinglove.slack

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityApiService
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubService
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandApiService
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubService
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.CancelSlackSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.ShuffleSlackSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.VerifySlackRequestUseCase
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackSlashCommandApiService(
                    apiServiceRegister = instance(),
                    jsonSerializer = instance(),
                    log = instance(),
                    verifySlackRequestUseCase = instance(),
                    slackConfig = instance(),
                    pubSubSender = instance()
                )
            }
            bindSingleton {
                provideSlackSlashCommandPubSubService(
                    pubSubServiceRegister = instance(),
                    jsonSerializer = instance(),
                    log = instance(),
                    slackRepository = instance(),
                    pubSubSender = instance(),
                    searchUseCase = instance()
                )
            }
            bindSingleton {
                provideSlackInteractivityApiService(
                    apiServiceRegister = instance(),
                    jsonSerializer = instance(),
                    log = instance(),
                    verifySlackRequestUseCase = instance(),
                    slackConfig = instance(),
                    pubSubSender = instance()
                )
            }
            bindSingleton {
                provideSlackInteractivityPubSubService(
                    pubSubServiceRegister = instance(),
                    jsonSerializer = instance(),
                    log = instance(),
                    shuffleSlackSearchUseCase = instance(),
                    cancelSlackSearchUseCase = instance(),
                    pubSubSender = instance(),
                )
            }
        }
    }

    private fun provideSlackSlashCommandApiService(
        apiServiceRegister: ApiServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        verifySlackRequestUseCase: VerifySlackRequestUseCase,
        slackConfig: SlackConfig,
        pubSubSender: PubSubSender,
    ): SlackSlashCommandApiService = SlackSlashCommandApiService(
        apiServiceRegister = apiServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        verifySlackRequestUseCase = verifySlackRequestUseCase,
        slackConfig = slackConfig,
        pubSubSender = pubSubSender
    )

    private fun provideSlackSlashCommandPubSubService(
        pubSubServiceRegister: PubSubServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        slackRepository: SlackRepository,
        pubSubSender: PubSubSender,
        searchUseCase: SearchUseCase
    ): SlackSlashCommandPubSubService = SlackSlashCommandPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        slackRepository = slackRepository,
        pubSubSender = pubSubSender,
        searchUseCase = searchUseCase
    )

    private fun provideSlackInteractivityApiService(
        apiServiceRegister: ApiServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        verifySlackRequestUseCase: VerifySlackRequestUseCase,
        slackConfig: SlackConfig,
        pubSubSender: PubSubSender,
    ): SlackInteractivityApiService = SlackInteractivityApiService(
        apiServiceRegister = apiServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        verifySlackRequestUseCase = verifySlackRequestUseCase,
        slackConfig = slackConfig,
        pubSubSender = pubSubSender
    )

    private fun provideSlackInteractivityPubSubService(
        pubSubServiceRegister: PubSubServiceRegister,
        jsonSerializer: Json,
        log: Logger,
        shuffleSlackSearchUseCase: ShuffleSlackSearchUseCase,
        cancelSlackSearchUseCase: CancelSlackSearchUseCase,
        pubSubSender: PubSubSender,
    ): SlackInteractivityPubSubService = SlackInteractivityPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        shuffleSlackSearchUseCase = shuffleSlackSearchUseCase,
        cancelSlackSearchUseCase = cancelSlackSearchUseCase,
        pubSubSender = pubSubSender
    )
}