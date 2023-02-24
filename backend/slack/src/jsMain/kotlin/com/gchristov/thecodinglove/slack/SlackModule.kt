package com.gchristov.thecodinglove.slack

import com.gchristov.thecodinglove.commonservicedata.api.ApiServiceRegister
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubSender
import com.gchristov.thecodinglove.commonservicedata.pubsub.PubSubServiceRegister
import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.searchdata.usecase.SearchWithSessionUseCase
import com.gchristov.thecodinglove.slack.usecase.RealVerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.SlackRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.VerifySlackRequestUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackModule : DiModule() {
    override fun name() = "slack"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindProvider { provideVerifySlackRequestUseCase(slackConfig = instance()) }
            bindSingleton {
                provideSlackSlashCommandApiService(
                    apiServiceRegister = instance(),
                    jsonSerializer = instance(),
                    verifySlackRequestUseCase = instance(),
                    slackConfig = instance(),
                    pubSubSender = instance()
                )
            }
            bindSingleton {
                provideSlackSlashCommandPubSubService(
                    pubSubServiceRegister = instance(),
                    jsonSerializer = instance(),
                    slackRepository = instance(),
                    pubSubSender = instance(),
                    searchWithSessionUseCase = instance()
                )
            }
        }
    }

    private fun provideVerifySlackRequestUseCase(
        slackConfig: SlackConfig
    ): VerifySlackRequestUseCase = RealVerifySlackRequestUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        clock = Clock.System
    )

    private fun provideSlackSlashCommandApiService(
        apiServiceRegister: ApiServiceRegister,
        jsonSerializer: Json,
        verifySlackRequestUseCase: VerifySlackRequestUseCase,
        slackConfig: SlackConfig,
        pubSubSender: PubSubSender,
    ): SlackSlashCommandApiService = SlackSlashCommandApiService(
        apiServiceRegister = apiServiceRegister,
        jsonSerializer = jsonSerializer,
        verifySlackRequestUseCase = verifySlackRequestUseCase,
        slackConfig = slackConfig,
        pubSubSender = pubSubSender
    )

    private fun provideSlackSlashCommandPubSubService(
        pubSubServiceRegister: PubSubServiceRegister,
        jsonSerializer: Json,
        slackRepository: SlackRepository,
        pubSubSender: PubSubSender,
        searchWithSessionUseCase: SearchWithSessionUseCase
    ): SlackSlashCommandPubSubService = SlackSlashCommandPubSubService(
        pubSubServiceRegister = pubSubServiceRegister,
        jsonSerializer = jsonSerializer,
        slackRepository = slackRepository,
        pubSubSender = pubSubSender,
        searchWithSessionUseCase = searchWithSessionUseCase
    )
}