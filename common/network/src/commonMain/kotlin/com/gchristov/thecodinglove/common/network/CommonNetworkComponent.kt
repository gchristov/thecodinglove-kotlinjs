package com.gchristov.thecodinglove.common.network

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.network.http.HttpService
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface CommonNetworkComponent {
    @Provides
    @Singleton
    fun provideHtmlNetworkClient(): NetworkClient.Html = NetworkClient.Html

    @Provides
    @Singleton
    fun provideJsonNetworkClient(jsonSerializer: JsonSerializer.Default): NetworkClient.Json =
        NetworkClient.Json(jsonSerializer)

    @Provides
    fun httpService(log: Logger): HttpService = provideHttpService(log)
}
