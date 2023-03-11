package com.gchristov.thecodinglove.slackdata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.*
import io.ktor.client.*
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
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
                    log = instance()
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
                )
            }
            bindProvider {
                provideSlackAuthUserUseCase(
                    slackConfig = instance(),
                    log = instance(),
                    slackRepository = instance(),
                )
            }
        }
    }

    private fun provideSlackApi(client: HttpClient) = SlackApi(client)

    private fun provideSlackConfig(): SlackConfig = SlackConfig(
        signingSecret = BuildKonfig.SLACK_SIGNING_SECRET,
        timestampValidityMinutes = 5,
        requestVerificationEnabled = BuildKonfig.SLACK_REQUEST_VERIFICATION_ENABLED,
        clientId = BuildKonfig.SLACK_CLIENT_ID,
        clientSecret = BuildKonfig.SLACK_CLIENT_SECRET,
    )

    private fun provideSlackRepository(
        api: SlackApi,
        log: Logger
    ): SlackRepository = RealSlackRepository(
        apiService = api,
        log = log
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
    ): SlackSendSearchUseCase = RealSlackSendSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchRepository = searchRepository,
        slackRepository = slackRepository
    )

    private fun provideSlackAuthUserUseCase(
        slackConfig: SlackConfig,
        log: Logger,
        slackRepository: SlackRepository,
    ): SlackAuthUserUseCase = RealSlackAuthUserUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        log = log,
        slackRepository = slackRepository,
    )
}