package com.gchristov.thecodinglove.slackdata

import com.gchristov.thecodinglove.kmpcommondi.DiModule
import com.gchristov.thecodinglove.slackdata.domain.SlackConfig
import io.ktor.client.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
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
                    jsonSerializer = instance()
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
        jsonSerializer: Json
    ): SlackRepository = RealSlackRepository(
        apiService = api,
        jsonSerializer = jsonSerializer
    )
}