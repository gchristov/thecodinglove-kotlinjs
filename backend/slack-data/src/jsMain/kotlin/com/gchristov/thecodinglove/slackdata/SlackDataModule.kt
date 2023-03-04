package com.gchristov.thecodinglove.slackdata

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.kmpcommonkotlin.di.DiModule
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import com.gchristov.thecodinglove.slackdata.usecase.CancelSlackSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.RealCancelSlackSearchUseCase
import com.gchristov.thecodinglove.slackdata.usecase.RealVerifySlackRequestUseCase
import com.gchristov.thecodinglove.slackdata.usecase.VerifySlackRequestUseCase
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
                provideVerifySlackRequestUseCase(
                    slackConfig = instance(),
                    log = instance()
                )
            }
            bindProvider {
                provideCancelSlackSearchUseCase(
                    log = instance(),
                    slackRepository = instance(),
                    searchRepository = instance(),
                )
            }
        }
    }

    private fun provideSlackApi(client: HttpClient) = SlackApi(client)

    private fun provideSlackConfig(): SlackConfig = SlackConfig(
        signingSecret = BuildKonfig.SLACK_SIGNING_SECRET,
        timestampValidityMinutes = 5,
        requestVerificationEnabled = BuildKonfig.SLACK_REQUEST_VERIFICATION_ENABLED
    )

    private fun provideSlackRepository(
        api: SlackApi,
        log: Logger
    ): SlackRepository = RealSlackRepository(
        apiService = api,
        log = log
    )

    private fun provideVerifySlackRequestUseCase(
        slackConfig: SlackConfig,
        log: Logger,
    ): VerifySlackRequestUseCase = RealVerifySlackRequestUseCase(
        dispatcher = Dispatchers.Default,
        slackConfig = slackConfig,
        clock = Clock.System,
        log = log
    )

    private fun provideCancelSlackSearchUseCase(
        log: Logger,
        searchRepository: SearchRepository,
        slackRepository: SlackRepository,
    ): CancelSlackSearchUseCase = RealCancelSlackSearchUseCase(
        dispatcher = Dispatchers.Default,
        log = log,
        searchRepository = searchRepository,
        slackRepository = slackRepository
    )
}