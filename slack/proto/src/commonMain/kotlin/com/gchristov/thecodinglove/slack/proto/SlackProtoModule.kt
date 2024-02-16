package com.gchristov.thecodinglove.slack.proto

import com.gchristov.thecodinglove.common.kotlin.di.DiModule
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.slack.proto.http.RealSlackServiceRepository
import com.gchristov.thecodinglove.slack.proto.http.SlackServiceApi
import com.gchristov.thecodinglove.slack.proto.http.SlackServiceRepository
import org.kodein.di.DI
import org.kodein.di.bindSingleton
import org.kodein.di.instance

class SlackProtoModule(private val apiUrl: String) : DiModule() {
    override fun name() = "slack-proto"

    override fun bindDependencies(builder: DI.Builder) {
        builder.apply {
            bindSingleton {
                provideSlackServiceApi(
                    networkClient = instance(),
                    apiUrl = apiUrl,
                )
            }
            bindSingleton {
                provideSlackServiceRepository(slackServiceApi = instance())
            }
        }
    }

    private fun provideSlackServiceApi(
        networkClient: NetworkClient.Json,
        apiUrl: String,
    ): SlackServiceApi = SlackServiceApi(
        client = networkClient,
        apiUrl = apiUrl,
    )

    private fun provideSlackServiceRepository(slackServiceApi: SlackServiceApi): SlackServiceRepository =
        RealSlackServiceRepository(slackServiceApi = slackServiceApi)
}