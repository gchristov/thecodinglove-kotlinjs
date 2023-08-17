package com.gchristov.thecodinglove.slackdata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.BuildConfig
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.kmpcommonnetwork.JsonClient
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.*
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.bindProvider
import org.kodein.di.bindSingleton
import org.kodein.di.instance

object SlackDataModule : DiModule() {
    override fun name() = "slack-data"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton { provideSlackApi(client = instance()) }
            bindSingleton { provideSlackConfig() }
            bindSingleton {
                provideSlackRepository(
                    api = instance(),
                    firebaseFirestore = instance(),
                )
            }
            bindProvider {
                provideSlackVerifyRequestUseCase(
                    slackConfig = instance(),
                    log = instance()
                )
            }
            bindProvider {
                provideSlackCancelSearchUseCase(
                    log = instance(),
                    slackRepository = instance(),
                    searchRepository = instance(),
                )
            }
            bindProvider {
                provideSlackShuffleSearchUseCase(
                    log = instance(),
                    searchUseCase = instance(),
                    slackRepository = instance(),
                )
            }
            bindProvider {
                provideSlackSendSearchUseCase(
                    log = instance(),
                    searchRepository = instance(),
                    slackRepository = instance(),
                    slackConfig = instance(),
                    jsonSerializer = instance(),
                )
            }
            bindProvider {
                provideSlackAuthUseCase(
                    slackConfig = instance(),
                    log = instance(),
                    slackRepository = instance(),
                )
            }
            bindProvider {
                provideSlackRevokeTokensUseCase(
                    log = instance(),
                    slackRepository = instance(),
                )
            }
        }
    }

    private fun provideSlackApi(client: JsonClient) = SlackApi(client)

    private fun provideSlackConfig(): SlackConfig = SlackConfig(
        signingSecret = BuildConfig.SLACK_SIGNING_SECRET,
        timestampValidityMinutes = 5,
        requestVerificationEnabled = BuildConfig.SLACK_REQUEST_VERIFICATION_ENABLED,
        clientId = BuildConfig.SLACK_CLIENT_ID,
        clientSecret = BuildConfig.SLACK_CLIENT_SECRET,
        interactivityPubSubTopic = BuildConfig.SLACK_INTERACTIVITY_PUB_SUB_TOPIC,
        slashCommandPubSubTopic = BuildConfig.SLACK_SLASH_COMMAND_PUB_SUB_TOPIC,
    )

    private fun provideSlackRepository(
        api: SlackApi,
        firebaseFirestore: FirebaseFirestore,
    ): SlackRepository = RealSlackRepository(
        apiService = api,
        firebaseFirestore = firebaseFirestore
    )

    private fun provideSlackVerifyRequestUseCase(
        slackConfig: SlackConfig,
        log: Logger,
    ): SlackVerifyRequestUseCase = RealSlackVerifyRequestUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        clock = Clock.System,
        log = log
    )

    private fun provideSlackCancelSearchUseCase(
        log: Logger,
        searchRepository: SearchRepository,
        slackRepository: SlackRepository,
    ): SlackCancelSearchUseCase = RealSlackCancelSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchRepository = searchRepository,
        slackRepository = slackRepository
    )

    private fun provideSlackShuffleSearchUseCase(
        log: Logger,
        searchUseCase: SearchUseCase,
        slackRepository: SlackRepository,
    ): SlackShuffleSearchUseCase = RealSlackShuffleSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchUseCase = searchUseCase,
        slackRepository = slackRepository
    )

    private fun provideSlackSendSearchUseCase(
        log: Logger,
        searchRepository: SearchRepository,
        slackRepository: SlackRepository,
        slackConfig: SlackConfig,
        jsonSerializer: Json,
    ): SlackSendSearchUseCase = RealSlackSendSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchRepository = searchRepository,
        slackRepository = slackRepository,
        slackConfig = slackConfig,
        jsonSerializer = jsonSerializer,
    )

    private fun provideSlackAuthUseCase(
        slackConfig: SlackConfig,
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackAuthUseCase = RealSlackAuthUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        log = log,
        slackRepository = slackRepository,
    )

    private fun provideSlackRevokeTokensUseCase(
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackRevokeTokensUseCase = RealSlackRevokeTokensUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        slackRepository = slackRepository,
    )
}