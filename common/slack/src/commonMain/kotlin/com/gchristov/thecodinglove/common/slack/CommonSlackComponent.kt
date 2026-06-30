package com.gchristov.thecodinglove.common.slack

import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonSlackComponent {
    @Provides
    @Singleton
    fun provideSlackSender(networkClient: NetworkClient.Json): SlackSender =
        SlackSender(client = networkClient)
}
