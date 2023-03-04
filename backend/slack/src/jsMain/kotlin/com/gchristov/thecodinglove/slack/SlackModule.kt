package com.gchristov.thecodinglove.slack

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityApiService
import com.gchristov.thecodinglove.slack.interactivity.SlackInteractivityPubSubService
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandApiService
import com.gchristov.thecodinglove.slack.slashcommand.SlackSlashCommandPubSubService
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.CancelSlackSearchUseCase
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
                    searchWithSessionUseCase = instance()
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
                    searchWithSessionUseCase = instance(),
                    cancelSlackSearchUseCase = instance(),
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
        searchWithSessionUseCase: SearchWithSessionUseCase
    ): SlackSlashCommandPubSubService = SlackSlashCommandPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        slackRepository = slackRepository,
        pubSubSender = pubSubSender,
        searchWithSessionUseCase = searchWithSessionUseCase
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
        searchWithSessionUseCase: SearchWithSessionUseCase,
        cancelSlackSearchUseCase: CancelSlackSearchUseCase,
    ): SlackInteractivityPubSubService = SlackInteractivityPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        log = log,
        searchWithSessionUseCase = searchWithSessionUseCase,
        cancelSlackSearchUseCase = cancelSlackSearchUseCase,
    )
}