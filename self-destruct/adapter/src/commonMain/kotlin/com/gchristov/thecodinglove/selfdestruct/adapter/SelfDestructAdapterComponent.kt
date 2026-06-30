package com.gchristov.thecodinglove.selfdestruct.adapter

import co.touchlab.kermit.Logger
import com.gchristov.thecodinglove.common.kotlin.JsonSerializer
import com.gchristov.thecodinglove.common.kotlin.di.Singleton
import com.gchristov.thecodinglove.common.network.NetworkClient
import com.gchristov.thecodinglove.selfdestruct.adapter.http.SelfDestructHttpHandler
import com.gchristov.thecodinglove.selfdestruct.adapter.slack.RealSelfDestructSlackRepository
import com.gchristov.thecodinglove.selfdestruct.adapter.slack.SelfDestructSlackServiceApi
import com.gchristov.thecodinglove.selfdestruct.domain.model.Environment
import com.gchristov.thecodinglove.selfdestruct.domain.port.SelfDestructSlackRepository
import com.gchristov.thecodinglove.selfdestruct.domain.usecase.SelfDestructUseCase
import kotlinx.coroutines.Dispatchers
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
interface SelfDestructAdapterComponent {
    @Provides
    @Singleton
    fun provideSelfDestructHttpHandler(
        jsonSerializer: JsonSerializer.Default,
        log: Logger,
        selfDestructUseCase: SelfDestructUseCase,
    ): SelfDestructHttpHandler = SelfDestructHttpHandler(
        dispatcher = Dispatchers.Default,
        jsonSerializer = jsonSerializer,
        log = log,
        selfDestructUseCase = selfDestructUseCase,
    )

    @Provides
    @Singleton
    fun provideSelfDestructSlackRepository(
        networkClient: NetworkClient.Json,
        environment: Environment,
    ): SelfDestructSlackRepository = RealSelfDestructSlackRepository(
        selfDestructSlackServiceApi = SelfDestructSlackServiceApi(
            client = networkClient,
            environment = environment,
        ),
    )
}
