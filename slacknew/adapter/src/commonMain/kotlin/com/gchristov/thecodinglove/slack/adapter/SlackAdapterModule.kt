package com.gchristov.thecodinglove.slack.adapter

import com.gchristov.thecodinglove.common.firebase.FirebaseAdmin
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.searchdata.SearchRepository
import com.gchristov.thecodinglove.searchdata.usecase.SearchUseCase
import com.gchristov.thecodinglove.slack.adapter.http.SlackApi
import com.gchristov.thecodinglove.slack.adapter.search.RealSearchSessionShuffle
import com.gchristov.thecodinglove.slack.adapter.search.RealSearchSessionStorage
import com.gchristov.thecodinglove.slack.domain.model.SlackConfig
import com.gchristov.thecodinglove.slack.domain.ports.SearchSessionShuffle
import com.gchristov.thecodinglove.slack.domain.ports.SearchSessionStorage
import com.gchristov.thecodinglove.slack.domain.ports.SlackAuthStateSerializer
import com.gchristov.thecodinglove.slack.domain.ports.SlackRepository
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

    private fun provideSearchSessionShuffle(searchUseCase: SearchUseCase): SearchSessionShuffle =
        RealSearchSessionShuffle(searchUseCase = searchUseCase)
}